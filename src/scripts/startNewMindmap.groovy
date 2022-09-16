// @ExecutionModes({ON_SINGLE_NODE})
// https://github.com/freeplane/freeplane/discussions/662


import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.freeplane.features.attribute.mindmapmode.MAttributeController
import org.freeplane.features.icon.mindmapmode.MIconController
import org.freeplane.features.map.NodeModel
import org.freeplane.features.mode.Controller
import org.freeplane.features.mode.ModeController
import org.freeplane.features.styles.ConditionalStyleModel
import org.freeplane.features.styles.LogicalStyleKeys
import org.freeplane.features.url.UrlManager
import org.freeplane.features.url.mindmapmode.MFileManager

import javax.swing.*
import java.nio.file.Files
import java.nio.file.StandardCopyOption

Node sourceNode = node
NodeModel sourceModel = sourceNode.delegate
def sourceFile = sourceNode.mindMap.file
def targetFile = chooseTargetFile(sourceNode, sourceModel, sourceFile)
if (targetFile)
    createNewMapFromNode(sourceNode, sourceModel, sourceFile, targetFile)

// based on org.freeplane.features.url.mindmapmode.ExportBranchAction
static File chooseTargetFile(Node sourceNode, NodeModel sourceModel, File sourceFile) {
    def shortText = sourceNode.shortText.replaceAll(/ \.\.\.$/, '')
    def chooser = UITools.newFileChooser(sourceFile.parentFile)
    def targetFile_ = new File(sourceFile.parent, shortText + '.mm')
    chooser.selectedFile = targetFile_
    def fileManager = UrlManager.controller as MFileManager
    if (fileManager.fileFilter !== null)
        chooser.setFileFilter(fileManager.fileFilter)
    def returnVal = chooser.showSaveDialog(Controller.currentController.viewController.currentRootComponent)
    def canAct = false
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        targetFile_ = addMmExtensionIfMissing(chooser.selectedFile)
        canAct = targetFile_.exists() ? confirmOverwrite(targetFile_, sourceModel) : true
    }
    return canAct ? targetFile_ : null
}

static File addMmExtensionIfMissing(File target) {
    def extension = target.name.find(/\.mm$/)
    if (extension)
        return target
    else
        return new File(target.path + '.mm')
}

static boolean confirmOverwrite(File targetFile, NodeModel sourceModel) {
    def title = 'Confirm overwrite'
    def msg = "The file already exists:\n${targetFile.name}\nOverwire it?"
    def decision = UITools.showConfirmDialog(sourceModel, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)
    return decision == 0
}

def createNewMapFromNode(Node sourceNode, NodeModel sourceModel, File sourceFile, File targetFile) {
    sourceNode.mindMap.save(true)
    Files.copy(sourceNode.mindMap.file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
    def mapLoader = c.mapLoader(targetFile)
    mapLoader.withView()
    def targetMindMap = mapLoader.mindMap
    def targetRoot = targetMindMap.root
    NodeModel targetModel = targetRoot.delegate
    targetRoot.text = sourceNode.text
    targetRoot.detailsText = sourceNode.detailsText
    targetRoot.noteText = sourceNode.noteText
    targetRoot.attributes.clear()
    MAttributeController.controller.copyAttributesToNode(sourceModel, targetModel)
    copyFormatAndIconsBetween(sourceModel, targetModel)
    copyNodeConditionalStylesBetween(sourceModel, targetModel)
    targetRoot.children.each { it.delete() }
    if (config.getProperty('links') == 'relative') {
        targetRoot.link.uri = "${makeUri(targetFile.parentFile.relativePath(sourceFile))}#${sourceNode.id}".toURI()
        sourceNode.link.uri = makeUri(sourceFile.parentFile.relativePath(targetFile))
    } else {
        targetRoot.link.uri = new URI(sourceFile.toURI().toString() + '#' + sourceNode.id)
        sourceNode.link.uri = targetFile.toURI()
    }
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

/**
 * File#toURI() converts fsPaths to absolute paths
 * This method works around it to allow relative fsPaths
 * @param fsPath relative or absolute
 * @return URI
 */
static URI makeUri(String fsPath) {
    return makeUri(new File(fsPath))
}

static URI makeUri(File file) {
    if (file.absolute) {
        return file.toURI()
    } else {
        def prefixPath = file.absolutePath[0..<-file.path.size()]
        def prefixUriStr = new File(prefixPath).toURI().toString()
        def absoluteUriStr = file.toURI().toString()
        def relativeUriStr = absoluteUriStr[prefixUriStr.size()..-1]
        return relativeUriStr.toURI()
    }
}
