// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Paste"})
/*
 * Paste each copied node as a node with a link to the original and formula =link.node.text
 *
 * Extends https://www.freeplane.org/wiki/index.php/Scripts_collection#Access_nodes_from_clipboard
 */

import groovy.xml.XmlParser
import org.freeplane.api.Node as FPN
import org.freeplane.features.map.clipboard.MapClipboardController
import org.freeplane.features.map.clipboard.MindMapNodesSelection

import java.awt.datatransfer.Transferable

def copiedNodes = getNodesFromClipboard(getXml(MapClipboardController.controller.clipboardContents))
if (copiedNodes.size() > 0) {
    def toBeSelected = new LinkedList<FPN>()
    FPN target = node // temporary, for attrib
    def textAttrib = target.mindMap.root['pasteAsSymlinkText']
    def detailsAttrib = target.mindMap.root['pasteAsSymlinkDetails']
    def noteAttrib = target.mindMap.root['pasteAsSymlinkNote']
    c.statusInfo = "${textAttrib} | ${detailsAttrib}"
    c.selecteds.each { FPN targetLocalRoot ->
        copiedNodes.each { FPN source ->
            target = targetLocalRoot.createChild()
            target.link.node = source
            target.text = !textAttrib ? '=link.node.transformedText' : textAttrib.text.replaceAll(/^'=/, '=')
            if (!detailsAttrib) {
                if (source.details) target.detailsText = '=link.node.details ?: \'\''
            } else if (detailsAttrib.startsWith(/'=/)) {
                target.detailsText = detailsAttrib.text.drop(0)
            } else {
                switch (detailsAttrib.num0) {
                    case 1: target.detailsText = "#${source.id}"
                        break
                    case 2: target.detailsText = "${source.transformedText}"
                        break
                    case 3: target.detailsText = "#${source.id}\n${source.transformedText}"
                        break
                }
            }
            if (!noteAttrib) {
                if (source.note) target.note = '=link.node.note ?: \'\''
            } else if (noteAttrib.startsWith(/'=/)) {
                target.note = noteAttrib.text.drop(0)
            }
            toBeSelected.add(target)
        }
    }
    c.select(toBeSelected)
} else {
    c.statusInfo = 'pasteAsSymlink: got zero nodes from clipboard (!)'
}

private List<FPN> getNodesFromClipboard(String xml) {
    try {
        def parser = new XmlParser()
        return xml.split(MapClipboardController.NODESEPARATOR).collect { String it ->
            def xmlRootNode = parser.parseText(it)
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
