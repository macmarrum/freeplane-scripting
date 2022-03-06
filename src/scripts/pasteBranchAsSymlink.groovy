// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Paste"})
/*
 * Consider each copied node as a branch root.
 * Paste each node in the branch as a node with a link to the original and formula =link.node.text
 *
 * Extends https://www.freeplane.org/wiki/index.php/Scripts_collection#Access_nodes_from_clipboard
 */

import groovy.xml.XmlParser
import org.freeplane.api.Node as FPN
import org.freeplane.features.map.clipboard.MapClipboardController
import org.freeplane.features.map.clipboard.MindMapNodesSelection

import java.awt.datatransfer.Transferable

def copiedNodes = getNodesFromClipboard(getXml(MapClipboardController.controller.clipboardContents))
if (copiedNodes.size() != 1) {
    c.statusInfo = "ERROR: copied nodes size is ${copiedNodes.size()} -- expected 1"
    return
}
def toBeSelected = new LinkedList<FPN>()
FPN source = copiedNodes[0]
for (FPN targetLocalRoot : c.selecteds.collect()) {
    if (targetLocalRoot.children.size() > 0) {
        c.statusInfo = 'ERROR: the selected target already has children'
        continue
    }
    c.select(targetLocalRoot)
    menuUtils.executeMenuItems(['PasteAction',])
    convertToSymlinkRecursivelyAndAddToBeSelected(source, targetLocalRoot.children[0], toBeSelected)
    c.select(toBeSelected)
}

private List<FPN> getNodesFromClipboard(String xml) {
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

void convertToSymlinkRecursivelyAndAddToBeSelected(FPN source, FPN target, ArrayList<FPN> toBeSelected) {
    convertToSymlinkAndAddToBeSelected(source, target, toBeSelected)
    source.children.eachWithIndex { FPN sourceChild, int i ->
        convertToSymlinkRecursivelyAndAddToBeSelected(sourceChild, target.children[i], toBeSelected)
    }
}

void convertToSymlinkAndAddToBeSelected(FPN source, FPN target, ArrayList<FPN> toBeSelected) {
    def textAttrib = target.mindMap.root['pasteAsSymlinkText']
    def detailsAttrib = target.mindMap.root['pasteAsSymlinkDetails']
    def noteAttrib = target.mindMap.root['pasteAsSymlinkNote']
    target.link.node = source
    target.text = !textAttrib ? '=link.node.transformedText' : textAttrib.text.replaceAll(/^'=/, '=')
    if (!detailsAttrib) {
        if (source.details)
            target.detailsText = '=link.node.details ?: \'(none)\''
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
    toBeSelected.add(target)
}
