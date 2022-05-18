// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Paste"})
/*
 * To each selected node paste each copied node, adding a link to the original
 * Limitation: among the copied nodes, only the topmost ones are considered (no descendants)
 *
 * Extends https://www.freeplane.org/wiki/index.php/Scripts_collection#Access_nodes_from_clipboard
 */

import groovy.xml.XmlParser
import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.features.map.clipboard.MapClipboardController
import org.freeplane.features.map.clipboard.MindMapNodesSelection
import org.freeplane.features.map.mindmapmode.clipboard.MMapClipboardController
import org.freeplane.plugin.script.proxy.ScriptUtils

import java.awt.datatransfer.Transferable

Controller c = ScriptUtils.c()
final Transferable t = (MapClipboardController.controller as MMapClipboardController).clipboardContents
def copiedNodes = getNodesFromClipboardXml(getXml(t))
if (copiedNodes.size() > 0) {
    def toBeSelected = new LinkedList<Node>()
    Node target
    c.selecteds.each { Node targetLocalRoot ->
        copiedNodes.each { Node source ->
            target = targetLocalRoot.createChild(source.text)
            target.link.node = source
            target.icons.addAll(source.icons)
            target.style.name = source.style.name
            target.detailsText = source.detailsText
            target.noteText = source.noteText
            source.attributes.each {entry ->
                target[entry.key] = entry.value
            }
            //TODO node conditionals
            toBeSelected.add(target)
        }
    }
    c.select(toBeSelected)
} else {
    c.statusInfo = 'pasteWithBacklink: got zero nodes from clipboard'
}

private static List<Node> getNodesFromClipboardXml(String xml) {
    try {
        def parser = new XmlParser()
        return xml.split(MapClipboardController.NODESEPARATOR).collect { String xmlSingleNode ->
            // replace &nbsp; to avoid the error: nbsp was referenced but not declared
            def xmlRootNode = parser.parseText(xmlSingleNode.replaceAll('&nbsp;', ' '))
            Node node = ScriptUtils.node()
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
