// https://github.com/freeplane/freeplane/discussions/792
import org.freeplane.core.util.LogUtils
import org.freeplane.plugin.script.ExecuteScriptException
import org.freeplane.plugin.script.ScriptingEngine

def port = 48112
def shouldPrintScript = false

def scriptHeader = '''\
import org.freeplane.plugin.script.proxy.ScriptUtils
def node = ScriptUtils.node()
'''

new Thread(() -> {
    def server = new ServerSocket()
    server.bind(new InetSocketAddress('127.0.0.1', port), 1)
    LogUtils.info("Groovy scripting server sterted on port ${server.localPort}")

    while (true) {
        server.accept(false) { socket ->
            LogUtils.info("Groovy scripting server connection: ${socket}")
            socket.withStreams { input, output ->
                // Avoid using the Groovy readLine(), as it closes input
                // and, apparently because of that,
                // output cannot be written back to socket
                def br = new BufferedReader(new InputStreamReader(input))
                def sb = new StringBuilder(scriptHeader)
                String line
                while ((line = br.readLine()) !== null) {
                    sb << line << '\n'
                }
                def script = sb.toString()
                if (shouldPrintScript)
                    print(script)
                try {
                    ScriptingEngine.executeScript(null, script)
                    output.withWriter { it << 'OK' }
                } catch (ExecuteScriptException e) {
                    output.withPrintWriter { e.printStackTrace(it) }
                }
            }
        }
    }
}, 'scripting-server').start()
