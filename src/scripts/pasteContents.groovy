// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Paste"})


import groovy.xml.XmlParser
import org.freeplane.api.Node
import org.freeplane.features.icon.mindmapmode.MIconController
import org.freeplane.features.map.NodeModel
import org.freeplane.features.map.clipboard.MapClipboardController
import org.freeplane.features.map.clipboard.MindMapNodesSelection
import org.freeplane.features.map.mindmapmode.clipboard.MMapClipboardController
import org.freeplane.features.mode.Controller
import org.freeplane.features.mode.ModeController
import org.freeplane.features.styles.ConditionalStyleModel
import org.freeplane.features.styles.LogicalStyleKeys

import java.awt.datatransfer.Transferable

//Controller c = ScriptUtils.c()
//Node node = ScriptUtils.node()
final Transferable t = ((MMapClipboardController) MapClipboardController.controller).clipboardContents
def copiedNodes = getNodesFromClipboardXml(getXml(t))
if (copiedNodes.size() == 1) {
    Node source = copiedNodes[0]
    NodeModel sourceModel = source.delegate
    c.selecteds.each { Node target ->
        NodeModel targetModel = target.delegate
        target.text = source.text
        target.detailsText = source.detailsText
        target.noteText = source.noteText
        target.attributes.clear()
        //TODO attribute key can be repeated, fix copying such attributes
        source.attributes.each { entry ->
            target[entry.key] = entry.value
        }
        copyFormatAndIconsBetween(sourceModel, targetModel)
        copyNodeConditionalStylesBetween(sourceModel, targetModel)
    }
} else {
    c.statusInfo = "pasteValuesIntoExistingNode: got ${copiedNodes.size()} nodes from clipboard -- expected 1"
}

private List<Node> getNodesFromClipboardXml(String xml) {
//    Node node = ScriptUtils.node()
    try {
        def parser = new XmlParser()
        return xml.split(MapClipboardController.NODESEPARATOR).collect { String xmlSingleNode ->
            // replace &nbsp; to avoid the error: nbsp was referenced but not declared
            def xmlRootNode = parser.parseText(xmlSingleNode.replaceAll('&nbsp;', ' '))
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
static copyFormatAndIconsBetween(NodeModel source, NodeModel target) {
    final ModeController modeController = Controller.getCurrentModeController()
    modeController.undoableRemoveExtensions(LogicalStyleKeys.LOGICAL_STYLE, target, target)
    modeController.undoableCopyExtensions(LogicalStyleKeys.LOGICAL_STYLE, source, target)
    modeController.undoableRemoveExtensions(LogicalStyleKeys.NODE_STYLE, target, target)
    modeController.undoableCopyExtensions(LogicalStyleKeys.NODE_STYLE, source, target)
    //if(ResourceController.getResourceController().getBooleanProperty("copyFormatToNewNodeIncludesIcons")) {
    modeController.undoableRemoveExtensions(MIconController.Keys.ICONS, target, target)
    modeController.undoableCopyExtensions(MIconController.Keys.ICONS, source, target)
    //}
}
