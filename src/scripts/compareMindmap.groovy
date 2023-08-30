// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})


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

class MindMapComparator {
    public static String stylePrefix = 'diff:'
    public static style = [
            NEW        : "${stylePrefix}NEW",
            DEL        : "${stylePrefix}DEL",
            MV_parent  : "${stylePrefix}MV-parent",
            MV_position: "${stylePrefix}MV-position",
            CH_text    : "${stylePrefix}CH-text",
            CH_details : "${stylePrefix}CH-details",
            CH_note    : "${stylePrefix}CH-note",
    ]
    public static inclinations = [[130, -60], [130, -20]]
    public static final ADD_CONNECTOR_ACTION_LIST = ['AddConnectorAction']

    /**
     * Compare two mind maps. Mark differences in newMindMap:
     * new node
     * deleted node
     * moved node
     * changed core
     * changed details
     * changed note
     */
    static void compare(MindMap newMindMap, MindMap oldMindMap) {
        createStylesIfMissing(newMindMap.delegate)
        compareNodeRecursively(newMindMap.root, oldMindMap)
        // add deleted nodes to mark them as DEL
        def c = ScriptUtils.c()
        oldMindMap.root.findAll().each { Node it ->
            if (!newMindMap.node(it.id)) {
                def parent = newMindMap.node(it.parent.id)
                def n = parent.appendChild(it)
                n.left = it.left
                n.moveTo(parent, it.parent.getChildPosition(it))
                n.conditionalStyles.insert(0, true, null, style.DEL, false)
                c.select(n)
                MenuUtils.executeMenuItems(ADD_CONNECTOR_ACTION_LIST)
            }
        }
        c.select(newMindMap.root)
    }

    static void compareNodeRecursively(Node node, MindMap oldMindMap) {
        def c = ScriptUtils.c()
        def oldNode = oldMindMap.node(node.id)
        c.select(node)
        if (oldNode == null) {
            // no such node in oldMindMap
            node.conditionalStyles.insert(0, true, null, style.NEW, false)
            MenuUtils.executeMenuItems(ADD_CONNECTOR_ACTION_LIST)
        } else {
            if (node.text != oldNode.text) {
                node.conditionalStyles.insert(0, true, null, style.CH_text, false)
                MenuUtils.executeMenuItems(ADD_CONNECTOR_ACTION_LIST)
            }
            if (node.detailsText != oldNode.detailsText) {
                node.conditionalStyles.insert(0, true, null, style.CH_details, false)
                MenuUtils.executeMenuItems(ADD_CONNECTOR_ACTION_LIST)
            }
            if (node.noteText != oldNode.noteText) {
                node.conditionalStyles.insert(0, true, null, style.CH_note, false)
                MenuUtils.executeMenuItems(ADD_CONNECTOR_ACTION_LIST)
            }
            if (!node.root) {
                if (node.parent.id != oldNode.parent.id) {
                    // parent changed
                    node.conditionalStyles.insert(0, true, null, style.MV_parent, false)
                    MenuUtils.executeMenuItems(ADD_CONNECTOR_ACTION_LIST)
                } else if (node.parent.getChildPosition(node) != oldNode.parent.getChildPosition(oldNode)) {
                    // position changed
                    node.conditionalStyles.insert(0, true, null, style.MV_position, false)
                    MenuUtils.executeMenuItems(ADD_CONNECTOR_ACTION_LIST)
                }
            }
        }
        node.children.each { Node nChild ->
            compareNodeRecursively(nChild, oldMindMap)
        }
    }

    static void createStylesIfMissing(MapModel mapModel) {
        createDiffStyleIfMissing(mapModel, style.NEW, '#52D273', 'LINE')
        createDiffStyleIfMissing(mapModel, style.DEL, '#f9556b', 'LINE')
        createDiffStyleIfMissing(mapModel, style.MV_parent, '#e57255ff', 'LINE')
        createDiffStyleIfMissing(mapModel, style.MV_position, '#e57255ff', 'LINE')
        createDiffStyleIfMissing(mapModel, style.CH_text, '#e5c453', 'CUBIC_CURVE')
        createDiffStyleIfMissing(mapModel, style.CH_details, '#d349a4', 'LINEAR_PATH')
        createDiffStyleIfMissing(mapModel, style.CH_note, '#46bddf', 'LINEAR_PATH')
    }

    static void createDiffStyleIfMissing(MapModel mapModel, String styleName, String colorCode, String shape) {
        def mapStyleModel = MapStyleModel.getExtension(mapModel)
        def iStyle = StyleFactory.create(styleName)
        def styleNode = mapStyleModel.getStyleNode(iStyle)
        if (!styleNode) {
            def nodeModel = createUserDefStyle(mapModel, mapStyleModel, iStyle)
            def n = new NodeProxy(nodeModel, null)
            def conn = n.addConnectorTo(n)
            conn.middleLabel = styleName[stylePrefix.size()..-1]
            conn.colorCode = colorCode
            conn.shape = shape
            conn.width = 4
            conn.startArrow = true
            conn.endArrow = false
            conn.setInclination(*inclinations)
        }
    }

    static NodeModel createUserDefStyle(MapModel mapModel, MapStyleModel mapStyleModel, IStyle iStyle) {
        NodeModel styleNode = new NodeModel(mapModel)
        styleNode.setUserObject(iStyle)
//        def userStyleParentNode = mapStyleModel.styleMap.root.children.find { (it.userObject as StyleTranslatedObject).object == STYLES_USER_DEFINED }
        def userStyleParentNode = mapStyleModel.getStyleNodeGroup(mapStyleModel.styleMap, STYLES_USER_DEFINED)
//        (Controller.currentModeController.mapController as MMapController).insertNode(styleNode, userStyleParentNode) // event triggered
        userStyleParentNode.insert(styleNode, userStyleParentNode.childCount) // no event triggered
        mapStyleModel.addStyleNode(styleNode)
        return styleNode
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
}


if (!config.getBooleanProperty('assignsNodeDependantStylesToNewConnectors'))
    UITools.showMessage('Enable "Preferences…->Defaults->Connectors->Assigns node dependant styles to new connectors" and try again', JOptionPane.WARNING_MESSAGE)
else {
    def node = ScriptUtils.node()
    def c = ScriptUtils.c()
    def oldFile = MindMapComparator.askForFile()
    if (oldFile) {
        def oldMindMap = c.mapLoader(oldFile).mindMap
        def mindMap = c.mapLoader(node.mindMap.file).unsetMapLocation().withView().mindMap
        MindMapComparator.compare(mindMap, oldMindMap)
    }
}
