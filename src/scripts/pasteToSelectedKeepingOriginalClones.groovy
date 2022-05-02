// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Paste"})

/*
 * Extends https://www.freeplane.org/wiki/index.php/Scripts_collection#Paste_clipboard
 *
 * MindMapNodesSelection.mindMapNodesFlavor = new DataFlavor("text/freeplane-nodes; class=java.lang.String");
 * MindMapNodesSelection.mindMapNodeObjectsFlavor = new DataFlavor("application/freeplane-nodes; class=java.util.Collection");
 * MindMapNodesSelection.mindMapNodeSingleObjectsFlavor = new DataFlavor("application/freeplane-single-nodes; class=java.util.Collection");
 * MindMapNodesSelection.htmlFlavor = new DataFlavor("text/html; class=java.lang.String");
 * MindMapNodesSelection.fileListFlavor = new DataFlavor("application/x-java-file-list; class=java.util.List");
 * MindMapNodesSelection.dropActionFlavor = new DataFlavor("text/drop-action; class=java.lang.String");
 */
import groovy.xml.XmlParser
import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.features.map.clipboard.MapClipboardController
import org.freeplane.features.map.clipboard.MindMapNodesSelection
import org.freeplane.features.map.mindmapmode.clipboard.MMapClipboardController
import org.freeplane.plugin.script.proxy.ScriptUtils

import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable

Controller c = ScriptUtils.c()
final toBeSelected = new LinkedList<Node>()
final selecteds = c.selecteds.collect() as List<Node>
selecteds.each { sel ->
    pasteAt(sel, toBeSelected)
    sel.folded = false
}
c.select(toBeSelected)

private static pasteAt(Node target, List<Node> toBeSelected) {
    final Transferable t = (MapClipboardController.controller as MMapClipboardController).clipboardContents
    final sourceNodes = getNodesFromClipboard(getXml(t), target)
    sourceNodes.each { Node source ->
        toBeSelected << replicate(source, target)
    }
}

private static List<Node> getNodesFromClipboard(String xml, Node target) {
    try {
        def parser = new XmlParser()
        return xml.split(MapClipboardController.NODESEPARATOR).collect { String xmlSingleNode ->
            // replace &nbsp; to avoid the error: nbsp was referenced but not declared
            def xmlRootNode = parser.parseText(xmlSingleNode.replaceAll('&nbsp;', ' '))
            target.mindMap.node(xmlRootNode.@ID)
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
        println(":: tranferable is missing `${MindMapNodesSelection.mindMapNodesFlavor.mimeType}`")
        println(":: available transferables:")
        t.getTransferDataFlavors().each { DataFlavor dataFlavor ->
            print('* ')
            println(dataFlavor.mimeType)
        }
    }
    return null
}

private static Node replicate(Node source, Node target) {
    Node targetChild
    final boolean canRecurse
    if (source.countNodesSharingContentAndSubtree > 0) {
        targetChild = target.appendAsCloneWithSubtree(source)
        canRecurse = false
    } else if (source.countNodesSharingContent > 0) {
        targetChild = target.appendAsCloneWithoutSubtree(source)
        canRecurse = true
    } else {
        targetChild = target.appendChild(source)
        canRecurse = true
    }
    if (canRecurse && source.children.size() > 0) {
        source.children.each { sourceChild ->
            replicate(sourceChild, targetChild)
        }
    }
    return targetChild
}
