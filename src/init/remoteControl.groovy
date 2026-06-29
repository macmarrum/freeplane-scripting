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


import org.freeplane.core.util.FreeplaneVersion
import org.freeplane.core.util.LogUtils
import org.freeplane.features.mode.Controller
import org.freeplane.features.script.IScriptStarter
import org.freeplane.plugin.script.proxy.ScriptUtils

import javax.swing.SwingUtilities
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

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
final ENCODING = 'UTF-8'
def timeout = 30  // TIMEOUT= on first line of script overrides it

new Thread({
    def server = new ServerSocket()
    server.bind(new InetSocketAddress(HOST, PORT), 1)
    LogUtils.info("Freeplane Remote Control started on ${server.inetAddress.hostAddress}:${server.localPort}" +
            " | FREEPLANE_REMOTE_CONTROL_PRINT_SCRIPT=$FREEPLANE_REMOTE_CONTROL_PRINT_SCRIPT")

    while (true) {
        server.accept(false) { socket ->
            socket.withStreams { input, output ->
//                def script = new String(input.readAllBytes(), ENCODING)  // readAllBytes() since Java 9
                def reader = new InputStreamReader(input, ENCODING)
                def sb = new StringBuilder()
                def buf = new char[4096]
                int n
                while ((n = reader.read(buf)) != -1) {
                    sb.append(buf, 0, n)
                }
                def script = sb.toString()

                def scriptLines = script.split('\n', 2)
                if (scriptLines[0].startsWith('TIMEOUT=')) {
                    try {
                        timeout = scriptLines[0].split('=')[1] as Integer
                    } catch (NumberFormatException ignored) {}
                    script = scriptLines.length > 1 ? scriptLines[1] : ''
                }

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
                        def starter = Controller.currentModeController.getExtension(IScriptStarter.class)
                        if (starter == null) {
                            resultFuture.complete('ERROR: Scripting not available (IScriptStarter extension not found)')
                            return
                        }
                        starter.executeScript(ScriptUtils.node().delegate, script)
                        resultFuture.complete("SUCCESS: ${ScriptUtils.node().id}")
                    } catch (Exception e) {
                        LogUtils.warn("Remote Control Script Error: ${e}")
                        resultFuture.complete("ERROR: ${e}")
                    }
                }

                try {
                    def response = resultFuture.get(timeout, TimeUnit.SECONDS)
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
