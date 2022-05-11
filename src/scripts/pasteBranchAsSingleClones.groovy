// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Paste"})
/*
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
final Transferable t = ((MMapClipboardController) MapClipboardController.controller).clipboardContents
def copiedNodes = getNodesFromClipboardXml(getXml(t))
def toBeSelected = new LinkedList<Node>()
copiedNodes.each { Node source ->
    c.selecteds.each { Node targetParent ->
        pasteAsSingleCloneRecursivelyAndAddToBeSelected(source, targetParent, toBeSelected)
    }
}
c.select(toBeSelected)

private static List<Node> getNodesFromClipboardXml(String xml) {
    Node node = ScriptUtils.node()
    try {
        def parser = new XmlParser()
        return xml.split(MapClipboardController.NODESEPARATOR).collect { String xmlSingleNode ->
            // replace &nbsp; to avoid the error: nbsp was referenced but not declared
            def xmlRootNode = parser.parseText(xmlSingleNode.replaceAll('&nbsp;', ' '))
            node.mindMap.node(xmlRootNode.@ID)
        }
    }
    catch (ignored) {
    }
    return []
}

private static String getXml(Transferable t) {
    if (t.isDataFlavorSupported(MindMapNodesSelection.mindMapNodesFlavor)) {
        try {
            return t.getTransferData(MindMapNodesSelection.mindMapNodesFlavor).toString()
        }
        catch (ignored) {
        }
    }
    return null
}

static void pasteAsSingleCloneRecursivelyAndAddToBeSelected(Node source, Node targetParent, List<Node> toBeSelected) {
    Node target = targetParent.appendAsCloneWithoutSubtree(source)
    if (target.visible)
        toBeSelected.add(target)
    source.children.eachWithIndex { Node sourceChild, int i ->
        pasteAsSingleCloneRecursivelyAndAddToBeSelected(sourceChild, target, toBeSelected)
    }
}
