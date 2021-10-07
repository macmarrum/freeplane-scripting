// @ExecutionModes({ON_SINGLE_NODE})
/*
 * Paste each copied node as a node with a link to the original and formula =link.node.text
 *
 * Extends https://www.freeplane.org/wiki/index.php/Scripts_collection#Access_nodes_from_clipboard
 */


import groovy.xml.XmlParser
import org.freeplane.features.map.clipboard.MapClipboardController
import org.freeplane.features.map.clipboard.MindMapNodesSelection

import java.awt.datatransfer.Transferable

private String getXml(Transferable t) {
    if (t.isDataFlavorSupported(MindMapNodesSelection.mindMapNodesFlavor)) {
        try {
            return t.getTransferData(MindMapNodesSelection.mindMapNodesFlavor).toString()
        }
        catch (final Exception e) {
        }
    }
    return null
}

private List getNodesFromClipboard(String xml) {
    try {
        def parser = new XmlParser()
        return xml.split(MapClipboardController.NODESEPARATOR).collect {
            def xmlRootNode = parser.parseText(it)
            node.mindMap.node(xmlRootNode.@ID)
        }
    }
    catch (final Exception e) {
    }
    return [];
}

def copiedNodes = getNodesFromClipboard(getXml(MapClipboardController.controller.clipboardContents))
if (copiedNodes.size() > 0) {
    def toBeSelected = new ArrayList()
    def child
    c.selecteds.each { self ->
        copiedNodes.each {
            child = self.createChild()
            child.link.node = it
            child.text = '=link.node.transformedText'
            child.detailsText = "<-${it.id}: ${it.transformedText}"
            toBeSelected.add(child)
        }
    }
    c.select(toBeSelected)
} else {
    c.statusInfo = 'pasteAsLinkNode: got zero nodes from clipboard (!)'
}