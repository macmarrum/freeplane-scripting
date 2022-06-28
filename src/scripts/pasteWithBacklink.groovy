// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Paste"})
/*
 * To each selected node paste each copied node, adding a link to the original
 * Limitation: among the copied nodes, only the topmost ones are considered (no descendants)
 *
 * Extends https://docs.freeplane.org/#/scripting/Scripts_collection#paste-clipboard
 */

import groovy.xml.XmlParser
import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.features.icon.mindmapmode.MIconController
import org.freeplane.features.map.NodeModel
import org.freeplane.features.map.clipboard.MapClipboardController
import org.freeplane.features.map.clipboard.MindMapNodesSelection
import org.freeplane.features.map.mindmapmode.clipboard.MMapClipboardController
import org.freeplane.features.mode.ModeController
import org.freeplane.features.styles.ConditionalStyleModel
import org.freeplane.features.styles.LogicalStyleKeys
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
            target.detailsText = source.detailsText
            target.noteText = source.noteText
            //TODO attribute key can be repeated, fix copying such attributes
            source.attributes.each { entry ->
                target[entry.key] = entry.value
            }
            copyNodeConditionalStylesBetween(source.delegate, target.delegate)
            copyFormatAndIconsBetween(source.delegate, target.delegate)
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

// based on org.freeplane.features.styles.mindmapmode.ManageNodeConditionalStylesAction.getConditionalStyleModel
private static copyNodeConditionalStylesBetween(NodeModel sourceModel, NodeModel targetModel) {
    targetModel.removeExtension(ConditionalStyleModel.class)
    ConditionalStyleModel sourceCondiStyleModel = sourceModel.getExtension(ConditionalStyleModel.class)
    if (sourceCondiStyleModel != null) {
        ConditionalStyleModel targetCondiStyleModel = new ConditionalStyleModel()
        targetModel.addExtension(targetCondiStyleModel)
        sourceCondiStyleModel.styles.each { targetCondiStyleModel.styles << new ConditionalStyleModel.Item(it) }
    }
}

// from org.freeplane.features.nodestyle.mindmapmode.PasteFormat
private static copyFormatAndIconsBetween(NodeModel source, NodeModel target) {
    final ModeController modeController = org.freeplane.features.mode.Controller.getCurrentModeController()
    modeController.undoableRemoveExtensions(LogicalStyleKeys.LOGICAL_STYLE, target, target)
    modeController.undoableCopyExtensions(LogicalStyleKeys.LOGICAL_STYLE, source, target)
    modeController.undoableRemoveExtensions(LogicalStyleKeys.NODE_STYLE, target, target)
    modeController.undoableCopyExtensions(LogicalStyleKeys.NODE_STYLE, source, target)
    //if(ResourceController.getResourceController().getBooleanProperty("copyFormatToNewNodeIncludesIcons")) {
    modeController.undoableRemoveExtensions(MIconController.Keys.ICONS, target, target)
    modeController.undoableCopyExtensions(MIconController.Keys.ICONS, source, target)
    //}
}
