/* remoteControlWS.groovy - a Freeplane remote-control server
 * Copyright (C) 2026  macmarrum (at) outlook (dot) ie
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
// https://github.com/freeplane/freeplane/discussions/792


import groovy.json.JsonSlurper
import org.freeplane.core.util.FreeplaneVersion
import org.freeplane.core.util.LogUtils
import org.freeplane.features.ai.code.AiCodeHostService
import org.freeplane.features.ai.code.CodeState
import org.freeplane.features.ai.code.CodeStateContent
import org.freeplane.features.ai.code.CodeStateToken
import org.freeplane.features.ai.code.ReadCodeRequest
import org.freeplane.features.ai.code.RunCodeRequest
import org.freeplane.features.ai.code.ScriptHost
import org.freeplane.features.ai.code.WriteCodeRequest
import org.freeplane.features.mode.Controller
import org.freeplane.features.script.IScriptStarter
import org.freeplane.plugin.script.proxy.ScriptUtils
import org.osgi.framework.FrameworkUtil

import javax.swing.SwingUtilities
import java.security.MessageDigest
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/*
 * Set FREEPLANE_REMOTE_CONTROL_HOST to '0.0.0.0' to enable access on all network interfaces
 * Set FREEPLANE_REMOTE_CONTROL_PORT to '0' for a random one
 * Set FREEPLANE_REMOTE_CONTROL_PRINT_SCRIPT to '1' to see in Freeplane console the script being executed;
 *     keep in mind to start Freeplane with a visible console, e.g. with freeplaneConsole.exe
 * Set FREEPLANE_REMOTE_CONTROL_EXECUTOR to AI_CODE_HOST, SCRIPTING_ENGINE, or AUTO (default)
 *     AI_CODE_HOST:  use AiCodeHostService; return error if unavailable
 *     SCRIPTING_ENGINE: use ScriptingEngine (IScriptStarter extension); return error if unavailable
 *     AUTO:          use AiCodeHostService if available, otherwise ScriptingEngine
 * Payload may be plain text (script only) or a JSON envelope:
 *     { "script": "...", "argumentsJson": "...", "timeout": N }
 *     SCRIPTING_ENGINE executor rejects argumentsJson
 */
final HOST = System.env.FREEPLANE_REMOTE_CONTROL_HOST ?: '127.0.0.1'
final FREEPLANE_REMOTE_CONTROL_PORT = System.env.FREEPLANE_REMOTE_CONTROL_PORT
final ver = FreeplaneVersion.version
final defaultPort = "48${ver.maj}${ver.mid}" as Integer
final PORT = FREEPLANE_REMOTE_CONTROL_PORT !== null ? System.env.FREEPLANE_REMOTE_CONTROL_PORT as Integer : defaultPort
final FREEPLANE_REMOTE_CONTROL_PRINT_SCRIPT = System.env.FREEPLANE_REMOTE_CONTROL_PRINT_SCRIPT
final FREEPLANE_REMOTE_CONTROL_EXECUTOR = System.env.FREEPLANE_REMOTE_CONTROL_EXECUTOR ?: 'AUTO'
final VALID_EXECUTORS = ['AI_CODE_HOST', 'SCRIPTING_ENGINE', 'AUTO']
if (FREEPLANE_REMOTE_CONTROL_EXECUTOR !in VALID_EXECUTORS) {
    LogUtils.severe("ERROR: illegal FREEPLANE_REMOTE_CONTROL_EXECUTOR — got ${FREEPLANE_REMOTE_CONTROL_EXECUTOR}, expected one of ${VALID_EXECUTORS}")
    return
}

static void debug(String message) {
    println("${new Date().format('HH:mm:ss')} ${message}")
}

static def parsePayload(String text) {
    try {
        def json = new JsonSlurper().parseText(text)
        if (json instanceof Map && json.containsKey('script')) {
            String argumentsJson = null
            if (json.containsKey('argumentsJson')) {
                argumentsJson = json.argumentsJson as String
            }
            def timeout = json.containsKey('timeout') ? json.timeout as Integer : 30
            return [script: json.script as String, argumentsJson: argumentsJson, timeout: timeout]
        }
    } catch (Exception ignored) {}
    return [script: text, argumentsJson: '', timeout: 30]
}

static def getAiCodeHostService() {
    try {
        def bundleContext = FrameworkUtil.getBundle(AiCodeHostService.class).getBundleContext()
        def refs = bundleContext.getServiceReferences(AiCodeHostService.class, '(scriptHost=AI)')
        return refs ? bundleContext.getService(refs.first()) : null
    } catch (Exception ignored) {
        return null
    }
}

// RFC 6455 magic GUID used in the WebSocket opening handshake
final WS_GUID = '258EAFA5-E914-47DA-95CA-C5AB0DC85B11'

/** Computes the Sec-WebSocket-Accept value from the client's Sec-WebSocket-Key. */
def wsAcceptKey(String clientKey, String guid) {
    def combined = clientKey.trim() + guid
    def sha1 = MessageDigest.getInstance('SHA-1').digest(combined.bytes)
    return Base64.encoder.encodeToString(sha1)
}

/**
 * Performs the WebSocket opening handshake.
 * Reads the HTTP Upgrade request from the input stream and writes the 101 response.
 * Returns the Sec-WebSocket-Key value, or null if the request is not a valid WebSocket upgrade.
 */
def wsHandshake(InputStream input, OutputStream output, String guid) {
    def reader = new BufferedReader(new InputStreamReader(input, 'UTF-8'))
    def headers = [:]
    String line
    // Read HTTP request headers (stop at empty line)
    while ((line = reader.readLine()) != null && !line.isEmpty()) {
        def colonIdx = line.indexOf(':')
        if (colonIdx > 0) {
            def name = line.substring(0, colonIdx).trim().toLowerCase()
            def value = line.substring(colonIdx + 1).trim()
            headers[name] = value
        }
    }
    def clientKey = headers['sec-websocket-key']
    if (!clientKey) return null

    def acceptKey = wsAcceptKey(clientKey, guid)
    def response = "HTTP/1.1 101 Switching Protocols\r\n" +
            "Upgrade: websocket\r\n" +
            "Connection: Upgrade\r\n" +
            "Sec-WebSocket-Accept: ${acceptKey}\r\n\r\n"
    output.write(response.bytes)
    output.flush()
    return clientKey
}

/**
 * Reads one WebSocket text frame from the input stream.
 * Handles masking as required by RFC 6455 for client→server frames.
 * Returns the decoded payload as a String, or null on connection close / unsupported opcode.
 */
def wsReadFrame(InputStream input) {
    def b0 = input.read()
    def b1 = input.read()
    if (b0 == -1 || b1 == -1) return null

    def opcode = b0 & 0x0F
    // opcode 8 = connection close
    if (opcode == 8) return null
    // we only handle text (1) and binary (2) frames
    if (opcode != 1 && opcode != 2) return null

    boolean masked = (b1 & 0x80) != 0
    long payloadLen = b1 & 0x7F

    if (payloadLen == 126) {
        payloadLen = ((input.read() & 0xFF) << 8) | (input.read() & 0xFF)
    } else if (payloadLen == 127) {
        payloadLen = 0
        8.times { payloadLen = (payloadLen << 8) | (input.read() & 0xFF) }
    }

    byte[] maskKey = new byte[4]
    if (masked) input.read(maskKey)

    byte[] payload = new byte[(int) payloadLen]
    int offset = 0
    while (offset < payloadLen) {
        def read = input.read(payload, offset, (int) (payloadLen - offset))
        if (read == -1) return null
        offset += read
    }

    if (masked) {
        payload.eachWithIndex { b, i -> payload[i] = (byte) (b ^ maskKey[i % 4]) }
    }

    return new String(payload, 'UTF-8')
}

/**
 * Sends a WebSocket text frame to the client.
 */
def wsSendText(OutputStream output, String text) {
    byte[] payload = text.getBytes('UTF-8')
    def out = new ByteArrayOutputStream()
    // FIN=1, opcode=1 (text)
    out.write(0x81)
    // No masking for server→client; set payload length
    if (payload.length < 126) {
        out.write(payload.length)
    } else if (payload.length < 65536) {
        out.write(126)
        out.write((payload.length >> 8) & 0xFF)
        out.write(payload.length & 0xFF)
    } else {
        out.write(127)
        8.times { i -> out.write((payload.length >> (56 - i * 8)) & 0xFF) }
    }
    out.write(payload)
    output.write(out.toByteArray())
    output.flush()
}

new Thread({
    def server = new ServerSocket()
    server.bind(new InetSocketAddress(HOST, PORT), 1)
    LogUtils.info("Freeplane Remote Control (WebSocket) started on ${server.inetAddress.hostAddress}:${server.localPort}" +
            " | FREEPLANE_REMOTE_CONTROL_EXECUTOR=$FREEPLANE_REMOTE_CONTROL_EXECUTOR" +
            " | FREEPLANE_REMOTE_CONTROL_PRINT_SCRIPT=$FREEPLANE_REMOTE_CONTROL_PRINT_SCRIPT" +
            '')

    while (true) {
        server.accept(false) { socket ->
            LogUtils.info("Freeplane Remote Control WebSocket connection: ${socket}")
            def input = socket.inputStream
            def output = socket.outputStream

            def wsKey = wsHandshake(input, output, WS_GUID)
            if (!wsKey) {
                LogUtils.warn('Freeplane Remote Control - invalid WebSocket handshake')
                socket.close()
                return
            }

            def text = wsReadFrame(input)
            if (text == null) {
                socket.close()
                return
            }

            def payload = parsePayload(text)
            def script = payload.script
            def argumentsJson = payload.argumentsJson
            def requestTimeout = payload.timeout

            if (FREEPLANE_REMOTE_CONTROL_PRINT_SCRIPT == '1')
                print(script)

            def resultFuture = new CompletableFuture<String>()
            SwingUtilities.invokeLater {
                try {
                    if (!ScriptUtils.c().openMindMaps) {
                        resultFuture.complete('ERROR: no mind map is open')
                        return
                    }

                    def useAiHost = false
                    AiCodeHostService service = null

                    if (FREEPLANE_REMOTE_CONTROL_EXECUTOR == 'AI_CODE_HOST' || FREEPLANE_REMOTE_CONTROL_EXECUTOR == 'AUTO') {
                        service = getAiCodeHostService()
                        if (service != null) {
                            useAiHost = true
                        } else if (FREEPLANE_REMOTE_CONTROL_EXECUTOR == 'AI_CODE_HOST') {
                            resultFuture.complete('ERROR: AiCodeHostService not available')
                            return
                        }
                    }

                    if (!useAiHost) {
                        if (argumentsJson != '') {
                            resultFuture.complete('ERROR: ScriptingEngine does not support arguments')
                            return
                        }
                        def starter = Controller.currentModeController.getExtension(IScriptStarter.class)
                        if (starter == null) {
                            resultFuture.complete('ERROR: Scripting not available (IScriptStarter extension not found)')
                            return
                        }
                        starter.executeScript(ScriptUtils.node().delegate, script)
                        resultFuture.complete("SUCCESS: ${ScriptUtils.node().id}")
                    } else {
                        def readResponse = service.readCode(new ReadCodeRequest(ScriptHost.AI))
                        if (readResponse.codeState == CodeState.WAITING_FOR_USER_RUN) {
                            resultFuture.complete("ERROR: another operation is already ${readResponse.codeState}")
                            return
                        }
                        def existingToken = readResponse.codeState == CodeState.NO_CODE ? null : readResponse.stateToken
                        debug("read -> existingToken: ${existingToken}")
                        def content = new CodeStateContent(script, argumentsJson)
                        debug("content stateToken: ${CodeStateToken.fromContent(content)}")
                        def writeResponse = service.writeCode(new WriteCodeRequest(ScriptHost.AI, content, existingToken))
                        debug("writeResponse.stateToken: ${writeResponse.stateToken}")
                        def runResponse = service.runCode(new RunCodeRequest(ScriptHost.AI, writeResponse.stateToken))
                        debug"runResponse.codeState: ${runResponse.codeState}"
                        switch (runResponse.codeState) {
                            case CodeState.WAITING_FOR_USER_RUN:
                                resultFuture.complete('WAITING: for user to run it')
                                break
                            case CodeState.RUN_SUCCEEDED:
                                resultFuture.complete("SUCCESS: ${runResponse.stdout ?: ''}")
                                break
                            case CodeState.RUN_FAILED:
                                resultFuture.complete("ERROR: ${runResponse.errorMessage ?: 'run failed'}")
                                break
                            default:
                                resultFuture.complete("ERROR: unexpected state: ${runResponse.codeState}")
                        }
                    }
                } catch (Exception e) {
                    LogUtils.warn("Remote Control Script Error: ${e}")
                    resultFuture.complete("ERROR: ${e}")
                }
            }

            try {
                def response = resultFuture.get(requestTimeout, TimeUnit.SECONDS)
                wsSendText(output, response)
            } catch (Exception e) {
                wsSendText(output, "ERROR: ${e}")
            }
        }
    }
}, 'freeplane-remote-control-websocket').start()
