// @ExecutionModes({ON_SINGLE_NODE})
// https://github.com/freeplane/freeplane/discussions/662

import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.freeplane.features.attribute.mindmapmode.MAttributeController
import org.freeplane.features.icon.mindmapmode.MIconController
import org.freeplane.features.link.LinkController
import org.freeplane.features.map.NodeModel
import org.freeplane.features.mode.Controller
import org.freeplane.features.mode.ModeController
import org.freeplane.features.styles.ConditionalStyleModel
import org.freeplane.features.styles.LogicalStyleKeys

import javax.swing.*
import java.nio.file.Files
import java.nio.file.StandardCopyOption

Node srcNode = node
NodeModel sourceModel = srcNode.delegate
def shortText = srcNode.shortText.replaceAll(/ \.\.\.$/, '')
def sourceFile = srcNode.mindMap.file
def targetFile = new File(sourceFile.parent, shortText + '.mm')

def canAct = true
if (targetFile.exists()) {
    def title = 'Confirm overwrite'
    def msg = "The file already exists:\n${targetFile.name}\nOverwire it?"
    def decision = UITools.showConfirmDialog(sourceModel, msg, title, JOptionPane.YES_OPTION, JOptionPane.WARNING_MESSAGE)
    canAct = decision == 0
}
if (canAct) createNewMapFromNode(srcNode, sourceModel, sourceFile, targetFile)

def createNewMapFromNode(Node srcNode, NodeModel sourceModel, File sourceFile, File targetFile) {
    srcNode.mindMap.save(true)
    Files.copy(srcNode.mindMap.file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
    def mapLoader = c.mapLoader(targetFile)
    mapLoader.withView()
    def targetMindMap = mapLoader.mindMap
    def trgRoot = targetMindMap.root
    NodeModel targetModel = trgRoot.delegate
    trgRoot.text = srcNode.text
    trgRoot.detailsText = srcNode.detailsText
    trgRoot.noteText = srcNode.noteText
    trgRoot.attributes.clear()
    def attributeController = MAttributeController.controller
    attributeController.copyAttributesToNode(sourceModel, targetModel)
    copyFormatAndIconsBetween(sourceModel, targetModel)
    copyNodeConditionalStylesBetween(sourceModel, targetModel)
    trgRoot.children.each { it.delete() }
    def linkController = LinkController.controller
    trgRoot.link.uri = linkController.createRelativeURI(targetFile.toURI(), new URI(sourceFile.toURI().toString() + '#' + srcNode.id))
    srcNode.link.uri = linkController.createRelativeURI(sourceFile.toURI(), targetFile.toURI())
    targetMindMap.save(true)
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
