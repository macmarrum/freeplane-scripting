// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Paste"})
/**
 * Paste each copied node as a node with a link to the original and formula
 * for core: =link.node.transformedText
 * for details: =link.node.details
 * for note: =link.node.note
 * i.e. plain text after formula evaluation
 *
 * Extends https://www.freeplane.org/wiki/index.php/Scripts_collection#Access_nodes_from_clipboard
 */


import groovy.xml.XmlParser
import org.freeplane.api.Node
import org.freeplane.features.map.clipboard.MapClipboardController
import org.freeplane.features.map.clipboard.MindMapNodesSelection
import org.freeplane.features.map.mindmapmode.clipboard.MMapClipboardController

import java.awt.datatransfer.Transferable

//Controller c = ScriptUtils.c()
//Node node = ScriptUtils.node()
final Transferable t = (MapClipboardController.controller as MMapClipboardController).clipboardContents
def copiedNodes = getNodesFromClipboardXml(getXml(t))
if (copiedNodes.size() > 0) {
    def toBeSelected = new LinkedList<Node>()
    Node target
    def root = node.mindMap.root
    def textAttrib = root['pasteAsSymlinkText']
    def detailsAttrib = root['pasteAsSymlinkDetails']
    def noteAttrib = root['pasteAsSymlinkNote']
    c.statusInfo = "${textAttrib} | ${detailsAttrib}"
    c.selecteds.each { Node targetLocalRoot ->
        copiedNodes.each { Node source ->
            target = targetLocalRoot.createChild()
            target.link.node = source
            target.text = !textAttrib ? '=link.node.transformedText' : textAttrib.text.replaceAll(/^[' ]=/, '=')
            if (!detailsAttrib) {
                if (source.details) target.detailsText = "=link.node.details ?: ''"
            } else if (detailsAttrib.text.matches(/^[' ]=/)) {
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
            } else if (noteAttrib.text.matches(/^[' ]=/)) {
                target.note = noteAttrib.text.drop(0)
            }
            toBeSelected.add(target)
        }
    }
    c.select(toBeSelected)
} else {
    c.statusInfo = 'pasteAsSymlink: got zero nodes from clipboard (!)'
}

private List<Node> getNodesFromClipboardXml(String xml) {
    try {
        def parser = new XmlParser()
        return xml.split(MapClipboardController.NODESEPARATOR).collect { String xmlSingleNode ->
            // replace &nbsp; to avoid the error: nbsp was referenced but not declared
            def xmlRootNode = parser.parseText(xmlSingleNode.replaceAll('&nbsp;', ' '))
//            Node node = ScriptUtils.node()
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
