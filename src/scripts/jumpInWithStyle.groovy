// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Jump"})


import org.freeplane.core.util.FreeplaneVersion
import org.freeplane.core.util.MenuUtils
import org.freeplane.features.icon.factory.IconStoreFactory
import org.freeplane.features.map.NodeModel
import org.freeplane.features.styles.MapStyleModel
import org.freeplane.features.styles.StyleFactory
import org.freeplane.features.styles.StyleTranslatedObject
import org.freeplane.plugin.script.proxy.NodeProxy
import org.freeplane.plugin.script.proxy.MapProxy
import org.freeplane.plugin.script.proxy.ScriptUtils

import static org.freeplane.features.styles.MapStyleModel.STYLES_AUTOMATIC_LAYOUT
import static org.freeplane.features.styles.MapStyleModel.STYLES_USER_DEFINED
import static org.freeplane.features.styles.AutomaticLayoutController.AUTOMATIC_LAYOUT_LEVEL_ROOT

// put the name of your custom style - it will be created if it doesn't exist
def jumpInStyleName = 'JumpIn'
// set to true for JumpIn to take on Auto-level-Root style
def shouldJumpInDeriveFromAutoLevelRoot = true
// put the background color code or '' to disable
def jumpInMapBackgroundColorCode = '#003333'

String jumpInIconName
if (FreeplaneVersion.version < new FreeplaneVersion(1, 11, 7)) {
    // for version before 1.11.7, put the name of the icon or '' to skip icon
    jumpInIconName = 'emoji-1F4CD' //'emoji-1F4A0'
} else {
    // since version 1.11.7, a pin icon is automatically shown after a jump-in
    jumpInIconName = ''
}

def node = ScriptUtils.node()
if (!node.isRoot()) {
    MenuUtils.executeMenuItems(['JumpInAction'])
    // add the style to mind map, if not exists
    def mapModel = (node.mindMap as MapProxy).delegate
    def mapStyleModel = MapStyleModel.getExtension(mapModel)
    def userStyleParentNode = mapStyleModel.getStyleNodeGroup(mapStyleModel.styleMap, STYLES_USER_DEFINED)
    def jumpInNodeModel = userStyleParentNode.children.find { it.text == jumpInStyleName }
    if (!jumpInNodeModel) {
        jumpInNodeModel = new NodeModel(mapModel)
        def iStyle = StyleFactory.create(jumpInStyleName)
        jumpInNodeModel.setUserObject(iStyle)
        userStyleParentNode.insert(jumpInNodeModel, userStyleParentNode.childCount) // no event triggered
        mapStyleModel.addStyleNode(jumpInNodeModel)
        if (jumpInIconName) {
            def jumpInIcon = IconStoreFactory.ICON_STORE.getMindIcon(jumpInIconName)
            jumpInNodeModel.addIcon(jumpInIcon)
        }
        if (shouldJumpInDeriveFromAutoLevelRoot) {
            def automaticLayoutParentNode = mapStyleModel.getStyleNodeGroup(mapStyleModel.styleMap, STYLES_AUTOMATIC_LAYOUT)
            if (automaticLayoutParentNode) {
                def styleRoot = automaticLayoutParentNode.children.find {
                    (it.userObject as StyleTranslatedObject).object.toString() == AUTOMATIC_LAYOUT_LEVEL_ROOT
                }
                if (styleRoot) {
                    def styleNode = new NodeProxy(jumpInNodeModel, null)
                    styleNode.conditionalStyles.add(true, null, styleRoot.text, false)
                }
            }
        }
    }
    def styleAlreadyAdded = node.conditionalStyles.find { it.active && it.always && it.styleName == jumpInStyleName && !it.last }
    if (!styleAlreadyAdded) {
        node.conditionalStyles.insert(0, true, null, jumpInStyleName, false)
    }
    if (jumpInMapBackgroundColorCode) {
        // save the original color to restore it on jump out
        ScriptUtils.c().viewRoot['preJumpInBackgroundColor'] = node.mindMap.backgroundColorCode ?: 'null'
        node.mindMap.backgroundColorCode = jumpInMapBackgroundColorCode
    }
//    if (FreeplaneVersion.version < new FreeplaneVersion(1, 11, 1)) {
//        // force view refresh
//        node.style.name = node.style.name
//    }
}
