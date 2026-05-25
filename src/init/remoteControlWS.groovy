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


import org.freeplane.core.util.FreeplaneVersion
import org.freeplane.core.util.LogUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

import java.security.MessageDigest

/*
 * Set FREEPLANE_REMOTE_CONTROL_HOST to '0.0.0.0' to enable access on all network interfaces
 * Set FREEPLANE_REMOTE_CONTROL_PORT to '0' for a random one
 * Set FREEPLANE_REMOTE_CONTROL_PRINT_SCRIPT to '1' to see in Freeplane console the script being executed;
 *     keep in mind to start Freeplane with a visible console, e.g. with freeplaneConsole.exe
 */
final HOST = System.env.FREEPLANE_REMOTE_CONTROL_HOST ?: '127.0.0.1'
final FREEPLANE_REMOTE_CONTROL_PORT = System.env.FREEPLANE_REMOTE_CONTROL_PORT
final ver = FreeplaneVersion.version
final defaultPort = "48${ver.maj}${ver.mid}" as Integer
final PORT = FREEPLANE_REMOTE_CONTROL_PORT !== null ? System.env.FREEPLANE_REMOTE_CONTROL_PORT as Integer : defaultPort
final FREEPLANE_REMOTE_CONTROL_PRINT_SCRIPT = System.env.FREEPLANE_REMOTE_CONTROL_PRINT_SCRIPT

final SCRIPT_HEADER = '''\
def __remoteControlReminderNode = node
'''
final SCRIPT_FOOTER = '''\
if (__remoteControlReminderNode)
    __remoteControlReminderNode.reminder.remove()
'''
final REMINDER_AFTER_MILLIS = 100
final REMINDER_PERIOD_UNIT = 'YEAR'
final REMINDER_PERIOD = 999

final SCHEDULED = 'SCHEDULED'
final ERROR = 'ERROR'

final ENCODING = 'UTF-8'

// RFC 6455 magic GUID used in the WebSocket opening handshake
final WS_GUID = '258EAFA5-E914-47DA-95CA-C5AB0DC85B11'

// --- WebSocket helpers ---

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

// --- Server thread ---

new Thread({
    def server = new ServerSocket()
    server.bind(new InetSocketAddress(HOST, PORT), 1)
    LogUtils.info("Freeplane Remote Control (WebSocket) started on ${server.inetAddress.hostAddress}:${server.localPort}" +
            " | FREEPLANE_REMOTE_CONTROL_PRINT_SCRIPT=$FREEPLANE_REMOTE_CONTROL_PRINT_SCRIPT")

    while (true) {
        server.accept(false) { socket ->
            LogUtils.info("Freeplane Remote Control WebSocket connection: ${socket}")
            def input = socket.inputStream
            def output = socket.outputStream

            // Perform WebSocket handshake
            def wsKey = wsHandshake(input, output, WS_GUID)
            if (!wsKey) {
                LogUtils.warn('Freeplane Remote Control - invalid WebSocket handshake')
                socket.close()
                return
            }

            // Read one text frame containing the Groovy script
            def scriptBody = wsReadFrame(input)
            if (scriptBody == null) {
                socket.close()
                return
            }

            def sb = new StringBuilder(SCRIPT_HEADER)
            sb << scriptBody << '\n' << SCRIPT_FOOTER
            def script = sb.toString()

            if (FREEPLANE_REMOTE_CONTROL_PRINT_SCRIPT == '1')
                print(script)

            if (!ScriptUtils.c().openMindMaps) {
                wsSendText(output, 'ERROR: no mind map is open')
                LogUtils.warn('Freeplane Remote Control - no mind map is open')
            } else {
                try {
                    def reminder = ScriptUtils.node().reminder
                    reminder.remove()
                    def remindAt = new Date(Calendar.instance.timeInMillis + REMINDER_AFTER_MILLIS)
                    reminder.createOrReplace(remindAt, REMINDER_PERIOD_UNIT, REMINDER_PERIOD)
                    reminder.script = script
                    wsSendText(output, SCHEDULED)
                } catch (Exception e) {
                    def sw = new StringWriter()
                    e.printStackTrace(new PrintWriter(sw))
                    wsSendText(output, "${ERROR}\n${sw}")
                    LogUtils.warn(e)
                }
            }

            socket.close()
        }
    }
}, 'freeplane-remote-control-websocket').start()
