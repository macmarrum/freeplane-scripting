// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})

import groovy.xml.XmlParser
import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.util.MenuUtils
import org.freeplane.features.map.clipboard.MapClipboardController
import org.freeplane.features.map.clipboard.MindMapNodesSelection
import org.freeplane.features.map.mindmapmode.clipboard.MMapClipboardController
import org.freeplane.plugin.script.proxy.ScriptUtils

import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable

def toBeSelected = new LinkedList<Node>()
final Transferable t = ((MMapClipboardController) MapClipboardController.controller).clipboardContents
def copiedNodes = getNodesFromClipboardXml(getXml(t))
Controller c = ScriptUtils.c()
List<Node> selecteds = c.selecteds.collect()
if (copiedNodes.size() != selecteds.size() && !(copiedNodes.size() == 1 && selecteds.size() > 1)) {
    c.statusInfo = "convertToClone: got ${copiedNodes.size()} nodes from clipboard and ${selecteds.size()} target nodes are selected -- expected the same count or 1 to many"
} else {
    def originalChildren
    Node source
    int position
    Node clone
    MenuUtils menuUtils = new MenuUtils()
    final copySingleAction = ['CopySingleAction']
    selecteds.eachWithIndex { target, i ->
        originalChildren = target.parent.children.collect()
        source = copiedNodes.size() == 1 ? copiedNodes[0] : copiedNodes[i]
        c.select(source)
        menuUtils.executeMenuItems(copySingleAction)
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

private List<Node> getNodesFromClipboardXml(String xml) {
    try {
        def parser = new XmlParser()
        return xml.split(MapClipboardController.NODESEPARATOR).collect { String it ->
            def xmlRootNode = parser.parseText(it)
            ScriptUtils.node().mindMap.node(xmlRootNode.@ID)
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
    } else {
        println(":: tranferable is missing ${MindMapNodesSelection.mindMapNodesFlavor}")
        println(":: available transferables:")
        t.getTransferDataFlavors().each { DataFlavor dataFlavor ->
            println(dataFlavor)
        }
    }
    return null
}
