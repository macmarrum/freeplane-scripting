// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
/*
 * Paste each copied node as a node with a link to the original and formula =link.node.text
 *
 * Extends https://www.freeplane.org/wiki/index.php/Scripts_collection#Access_nodes_from_clipboard
 */


import groovy.xml.XmlParser
import org.freeplane.features.map.clipboard.MapClipboardController
import org.freeplane.features.map.clipboard.MindMapNodesSelection

import java.awt.datatransfer.Transferable

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

private List getNodesFromClipboard(String xml) {
    try {
        def parser = new XmlParser()
        return xml.split(MapClipboardController.NODESEPARATOR).collect { String it ->
            def xmlRootNode = parser.parseText(it)
            node.mindMap.node(xmlRootNode.@ID)
        }
    }
    catch (ignored) {
    }
    return []
}

def copiedNodes = getNodesFromClipboard(getXml(MapClipboardController.controller.clipboardContents))
if (copiedNodes.size() > 0) {
    def toBeSelected = new ArrayList()
    def child
    def textAttrib = node.map.root['pasteAsLinkNodeText']
    def detailsAttrib = node.map.root['pasteAsLinkNodeDetails']
    c.statusInfo = "${textAttrib} | ${detailsAttrib}"
    c.selecteds.each { target ->
        copiedNodes.each { source ->
            child = target.createChild()
            child.link.node = source
            child.text = !textAttrib ? '=link.node.transformedText' : textAttrib.text.replaceAll(/^'=/, '=')
            if (!detailsAttrib) {
                if (source.details)
                    child.detailsText = '=link.node.details ?: \'(none)\''
            } else if (detailsAttrib.startsWith(/'=/)) {
                child.detailsText = detailsAttrib.text.drop(0)
            } else {
                switch (detailsAttrib.num0) {
                    case 1: child.detailsText = "#${source.id}"
                        break
                    case 2: child.detailsText = "${source.transformedText}"
                        break
                    case 3: child.detailsText = "#${source.id}\n${source.transformedText}"
                        break
                }
            }
            toBeSelected.add(child)
        }
    }
    c.select(toBeSelected)
} else {
    c.statusInfo = 'pasteAsLinkNode: got zero nodes from clipboard (!)'
}