// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Paste"})

import groovy.xml.XmlParser
import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.features.map.clipboard.MapClipboardController
import org.freeplane.features.map.clipboard.MindMapNodesSelection
import org.freeplane.features.map.mindmapmode.clipboard.MMapClipboardController
import org.freeplane.plugin.script.proxy.ScriptUtils

import java.awt.datatransfer.Transferable

//Controller c = ScriptUtils.c()
//Node node = ScriptUtils.node()
final Transferable t = ((MMapClipboardController) MapClipboardController.controller).clipboardContents
def copiedNodes = getNodesFromClipboardXml(getXml(t))
if (copiedNodes.size() == 1) {
    Node source = copiedNodes[0]
    c.selecteds.each { Node target ->
        target.text = source.text
        target.icons.clear()
        target.icons.addAll(source.icons)
        target.style.name = source.style.name
        target.detailsText = source.detailsText
        target.noteText = source.noteText
        target.attributes.clear()
        source.attributes.each {entry ->
            target[entry.key] = entry.value
        }
        //TODO node conditionals
    }
} else {
    c.statusInfo = "pasteValuesIntoExistingNode: got ${copiedNodes.size()} nodes from clipboard -- expected 1"
}

private List<Node> getNodesFromClipboardXml(String xml) {
//    Node node = ScriptUtils.node()
    try {
        def parser = new XmlParser()
        return xml.split(MapClipboardController.NODESEPARATOR).collect { String xmlSingleNode ->
            // replace &nbsp; to avoid the error: nbsp was referenced but not declared
            def xmlRootNode = parser.parseText(xmlSingleNode.replaceAll('&nbsp;', ' '))
            node.mindMap.node(xmlRootNode.@ID)
        }
    } catch (ignored) {
    }
    return []
}

private static String getXml(Transferable t) {
    if (t.isDataFlavorSupported(MindMapNodesSelection.mindMapNodesFlavor)) {
        try {
            return t.getTransferData(MindMapNodesSelection.mindMapNodesFlavor).toString()
        } catch (ignored) {
        }
    }
    return null
}
