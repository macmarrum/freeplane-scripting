// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
// https://github.com/freeplane/freeplane/discussions/1256

import org.freeplane.api.Node
import org.freeplane.plugin.script.proxy.ScriptUtils

def mindMap = ScriptUtils.node().mindMap
ScriptUtils.c().findAll().each { Node it ->
    def uri = it.link.uri
    if (uri && uri.scheme == 'freeplane' && uri.fragment.startsWith('ID_')) {
        def uriPath = uri.path.replaceFirst($/^/ /$, '')
        def file = new File(uriPath)
        if (file == mindMap.file) {
            it.link.node = N(uri.fragment)
        }
    }
}
