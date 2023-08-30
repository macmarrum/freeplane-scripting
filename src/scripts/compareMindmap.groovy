// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac3"})


import org.freeplane.api.MindMap
import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.freeplane.core.util.MenuUtils
import org.freeplane.features.map.MapModel
import org.freeplane.features.map.NodeModel
import org.freeplane.features.styles.IStyle
import org.freeplane.features.styles.MapStyleModel
import org.freeplane.features.styles.StyleFactory
import org.freeplane.plugin.script.proxy.NodeProxy
import org.freeplane.plugin.script.proxy.ScriptUtils

import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter

import static org.freeplane.features.styles.MapStyleModel.STYLES_USER_DEFINED

def node = ScriptUtils.node()
def c = ScriptUtils.c()

static void createStyles(MapModel mapModel) {
    createStyleWithConnector(mapModel, 'diff:NEW', 'NEW', '#52D273', 'LINE')
    createStyleWithConnector(mapModel, 'diff:DEL', 'DEL', '#f9556b', 'LINE')
    createStyleWithConnector(mapModel, 'diff:MV-parent', 'MV-parent', '#e57255ff', 'LINE')
    createStyleWithConnector(mapModel, 'diff:MV-position', 'MV-position', '#e57255ff', 'LINE')
    createStyleWithConnector(mapModel, 'diff:CH-text', 'CH-text', '#e5c453', 'CUBIC_CURVE')
    createStyleWithConnector(mapModel, 'diff:CH-details', 'CH-details', '#d349a4', 'LINEAR_PATH')
    createStyleWithConnector(mapModel, 'diff:CH-note', 'CH-note', '#46bddf', 'LINEAR_PATH')
}

static NodeModel getOrCreateUserDefStyle(MapModel mapModel, IStyle iStyle) {
    def mapStyleModel = MapStyleModel.getExtension(mapModel)
    def styleNode = mapStyleModel.getStyleNode(iStyle)
    if (!styleNode) {
        styleNode = new NodeModel(mapModel)
        styleNode.setUserObject(iStyle)
        def userStyleParentNode = mapStyleModel.getStyleNodeGroup(mapStyleModel.styleMap, STYLES_USER_DEFINED)
//        def userStyleParentNode = mapStyleModel.styleMap.root.children.find { (it.userObject as StyleTranslatedObject).object == STYLES_USER_DEFINED }
//        (Controller.currentModeController.mapController as MMapController).insertNode(styleNode, userStyleParentNode) // event triggered
        userStyleParentNode.insert(styleNode, userStyleParentNode.childCount) // no event triggered
        mapStyleModel.addStyleNode(styleNode)
    }
    return styleNode
}

static void createStyleWithConnector(MapModel mapModel, String styleName, String middleLabel, String colorCode, String shape) {
    def nodeModel = getOrCreateUserDefStyle(mapModel, StyleFactory.create(styleName))
    def n = new NodeProxy(nodeModel, null)
    if (!n.connectorsIn) {
        def conn = n.addConnectorTo(n)
        conn.middleLabel = middleLabel
        conn.colorCode = colorCode
        conn.shape = shape
        conn.startArrow = true
        conn.endArrow = false
    }
}

static File askForFile() {
    final fileChooser = new JFileChooser()
    fileChooser.dialogTitle = 'Select old mind map to compare'
    fileChooser.multiSelectionEnabled = false
    fileChooser.fileFilter = new FileNameExtensionFilter('Mind map', 'mm')
    fileChooser.currentDirectory = ScriptUtils.node().mindMap.file.parentFile
    final returnVal = fileChooser.showOpenDialog(UITools.currentRootComponent)
    if (returnVal != JFileChooser.APPROVE_OPTION) {
        return
    }
    return fileChooser.getSelectedFile()
}

/**
 * Compare two mind maps. Mark differences in newMindMap:
 * new
 * deleted
 * moved
 * changed core
 * changed details
 * changed note
 * 
 * Not implemented:
 * changed attributes
 * changed icons
 * changed style
 */
static void compare(MindMap newMindMap, MindMap oldMindMap) {
    if (newMindMap.root.id != oldMindMap.root.id) {
        UITools.showMessage('Mind maps have different root nodes', JOptionPane.ERROR_MESSAGE)
    } else {
        compareNodeRecursively(newMindMap.root, oldMindMap)
        def c = ScriptUtils.c()
        oldMindMap.root.findAll().each { Node it ->
            if (!newMindMap.node(it.id)) {
                def parent = newMindMap.node(it.parent.id)
                def n = parent.appendChild(it)
                n.left = it.left
                n.moveTo(parent, it.parent.getChildPosition(it))
                // only add style if parent wasn't deleted too
                def pcs = parent.conditionalStyles.collect()
                if (!pcs || !pcs.find { it.always && it.styleName == 'diff:DEL' && it.active && !it.last }) {
                    n.conditionalStyles.insert(0, true, null, 'diff:DEL', false)
                    c.select(n)
                    MenuUtils.executeMenuItems(['AddConnectorAction'])
                }
            }
        }
    }
}

static void compareNodeRecursively(Node node, MindMap oldMindMap) {
    def c = ScriptUtils.c()
    def oldNode = oldMindMap.node(node.id)
    c.select(node)
    if (oldNode == null) {
        // no such node in oldMindMap
        node.conditionalStyles.insert(0, true, null, 'diff:NEW', false)
        MenuUtils.executeMenuItems(['AddConnectorAction'])
    } else {
        if (node.text != oldNode.text) {
            node.conditionalStyles.insert(0, true, null, 'diff:CH-text', false)
            MenuUtils.executeMenuItems(['AddConnectorAction'])
        }
        if (node.detailsText != oldNode.detailsText) {
            node.conditionalStyles.insert(0, true, null, 'diff:CH-details', false)
            MenuUtils.executeMenuItems(['AddConnectorAction'])
        }
        if (node.noteText != oldNode.noteText) {
            node.conditionalStyles.insert(0, true, null, 'diff:CH-note', false)
            MenuUtils.executeMenuItems(['AddConnectorAction'])
        }
        if (!node.root) {
            if (node.parent.id != oldNode.parent.id) {
                // parent changed
                node.conditionalStyles.insert(0, true, null, 'diff:MV-parent', false)
                MenuUtils.executeMenuItems(['AddConnectorAction'])
            } else if (node.parent.getChildPosition(node) != oldNode.parent.getChildPosition(oldNode)) {
                // position changed
                node.conditionalStyles.insert(0, true, null, 'diff:MV-position', false)
                MenuUtils.executeMenuItems(['AddConnectorAction'])
            }
        }
        node.children.each { Node nChild ->
            compareNodeRecursively(nChild, oldMindMap)
        }
    }
}

if (!config.getBooleanProperty('assignsNodeDependantStylesToNewConnectors'))
    UITools.showMessage('Enable "Preferences…->Defaults->Connectors->Assigns node dependant styles to new connectors" and try again', JOptionPane.WARNING_MESSAGE)
else {
    def oldFile = askForFile()
    if (oldFile) {
        def oldMindMap = c.mapLoader(oldFile).mindMap
        def mindMap = c.mapLoader(node.mindMap.file).unsetMapLocation().withView().mindMap
        createStyles(mindMap.delegate)
        compare(mindMap, oldMindMap)
    }
}
