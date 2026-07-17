/* remoteControl.groovy - a Freeplane remote-control server
 * Copyright (C) 2023, 2024, 2026  macmarrum (at) outlook (dot) ie
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
final ENCODING = 'UTF-8'
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

new Thread({
    def server = new ServerSocket()
    server.bind(new InetSocketAddress(HOST, PORT), 1)
    LogUtils.info("Freeplane Remote Control started on ${server.inetAddress.hostAddress}:${server.localPort}" +
            " | FREEPLANE_REMOTE_CONTROL_EXECUTOR=$FREEPLANE_REMOTE_CONTROL_EXECUTOR" +
            " | FREEPLANE_REMOTE_CONTROL_PRINT_SCRIPT=$FREEPLANE_REMOTE_CONTROL_PRINT_SCRIPT" +
            '')

    while (true) {
        server.accept(false) { socket ->
            socket.withStreams { input, output ->
                def reader = new InputStreamReader(input, ENCODING)
                def sb = new StringBuilder()
                def buf = new char[4096]
                int n
                while ((n = reader.read(buf)) != -1) {
                    sb.append(buf, 0, n)
                }
                def text = sb.toString()
                def payload = parsePayload(text)
                def script = payload.script
                def argumentsJson = payload.argumentsJson
                def requestTimeout = payload.timeout

                if (FREEPLANE_REMOTE_CONTROL_PRINT_SCRIPT == '1') {
                    println(script)
                }

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
                    output.write(response.getBytes(ENCODING))
                    output.flush()
                } catch (Exception e) {
                    output.write("ERROR: ${e}".getBytes(ENCODING))
                    output.flush()
                }
            }
        }
    }
}, 'freeplane-remote-control').start()
