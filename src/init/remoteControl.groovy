/*
remoteControl.groovy - a Freeplane remote-control server
Copyright (C) 2023  macmarrum

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
// https://github.com/freeplane/freeplane/discussions/792

import org.freeplane.core.util.LogUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

/* Set PORT to 0 for a random one
 * Set HOSTNAME to '0.0.0.0' to enable access from outside
 * Set SHOULD_PRINT_SCRIPT to 1 to see in Freeplane console the script being executed;
 *     keep in mind to start Freeplane with a visible console, e.g. with freeplaneConsole.exe
 */
final ADDRESS = System.getenv('FREEPLANE_REMOTE_CONTROL_ADDRESS') ?: '127.0.0.1'
final PORT = System.getenv('FREEPLANE_REMOTE_CONTROL_PORT') as Integer ?: 48112
final SHOULD_PRINT_SCRIPT = System.getenv('FREEPLANE_REMOTE_CONTROL_PRINT_SCRIPT') == '1'

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

new Thread(() -> {
    def server = new ServerSocket()
    server.bind(new InetSocketAddress(ADDRESS, PORT), 1)
    LogUtils.info("Freeplane Remote Control started on ${server.inetAddress.hostAddress}:${server.localPort}")

    while (true) {
        server.accept(false) { socket ->
            LogUtils.info("Freeplane Remote Control connection: ${socket}")
            socket.withStreams { input, output ->
                // Avoid using the Groovy readLine(), as it closes input
                // and, apparently because of that,
                // output cannot be written back to socket
                def br = new BufferedReader(new InputStreamReader(input))
                def sb = new StringBuilder(SCRIPT_HEADER)
                String line
                while ((line = br.readLine()) !== null) {
                    sb << line << '\n'
                }
                sb << SCRIPT_FOOTER
                def script = sb.toString()
                if (SHOULD_PRINT_SCRIPT)
                    print(script)
                try {
                    def reminder = ScriptUtils.node().reminder
                    reminder.remove()
                    def remindAt = new Date(Calendar.instance.timeInMillis + REMINDER_AFTER_MILLIS)
                    reminder.createOrReplace(remindAt, REMINDER_PERIOD_UNIT, REMINDER_PERIOD)
                    reminder.script = script
                    output.withWriter {
                        it << SCHEDULED
                    }
                } catch (Exception e) {
                    output.withPrintWriter {
                        it.println(ERROR)
                        e.printStackTrace(it)
                    }
                    LogUtils.warn(e)
                }
            }
        }
    }
}, 'freeplane-remote-control').start()
