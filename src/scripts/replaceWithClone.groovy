// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})

import groovy.xml.XmlParser
import org.freeplane.api.Node as FPN
import org.freeplane.features.map.clipboard.MapClipboardController
import org.freeplane.features.map.clipboard.MindMapNodesSelection

import java.awt.datatransfer.Transferable

def toBeSelected = new LinkedList<FPN>()
def copiedNodes = getNodesFromClipboardXml(getXml(MapClipboardController.controller.clipboardContents))
List<FPN> selecteds = c.selecteds.collect()
if (copiedNodes.size() != selecteds.size()) {
    c.statusInfo = "convertToClone: got ${copiedNodes.size()} nodes from clipboard and ${selecteds.size()} target nodes are selected -- expected the same count"
} else {
    def originalChildren
    FPN source
    int position
    FPN clone
    selecteds.eachWithIndex { target, i ->
        originalChildren = target.parent.children.collect()
        source = copiedNodes[i]
        c.select(source)
        menuUtils.executeMenuItems(['CopySingleAction'])
        position = target.parent.getChildPosition(target)
        target.parent.pasteAsClone()
        clone = target.parent.children[-1]
        clone.moveTo(target.parent, position)
        target.children.each { it.moveTo(clone) }
        target.delete()
        toBeSelected << clone
    }
}
c.select(toBeSelected)

private List<FPN> getNodesFromClipboardXml(String xml) {
    try {
        def parser = new XmlParser()
        return xml.split(MapClipboardController.NODESEPARATOR).collect { String it ->
            def xmlRootNode = parser.parseText(it)
            N(xmlRootNode.@ID)
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
