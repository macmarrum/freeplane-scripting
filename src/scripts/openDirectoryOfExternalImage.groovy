// @ExecutionModes({ON_SELECTED_NODE="/menu_bar/Mac2"})
// based on OpenCurrentMapDirAction.java
import org.freeplane.core.util.Hyperlink
import org.freeplane.features.link.LinkController
import org.freeplane.plugin.script.proxy.ScriptUtils

def node = ScriptUtils.node()
def c = ScriptUtils.c()

if (node.externalObject.uri) {
    def file = new File(node.externalObject.uri)
    def uri = file.parentFile.toURI()
    LinkController.controller.loadHyperlink(new Hyperlink(uri))
} else {
    c.statusInfo = 'the node is missing an externalObject'
}

