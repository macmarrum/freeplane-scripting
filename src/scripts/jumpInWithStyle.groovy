// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Jump"})


import org.freeplane.core.util.FreeplaneVersion
import org.freeplane.core.util.MenuUtils
import org.freeplane.features.icon.factory.IconStoreFactory
import org.freeplane.features.map.NodeModel
import org.freeplane.features.styles.MapStyleModel
import org.freeplane.features.styles.StyleFactory
import org.freeplane.plugin.script.proxy.MapProxy
import org.freeplane.plugin.script.proxy.ScriptUtils

import static org.freeplane.features.styles.MapStyleModel.STYLES_USER_DEFINED

// put the name of your custom style - it will be created if it doesn't exist
def jumpInStyleName = 'JumpIn'
// put the name of the icon or '' to skip icon
def jumpInIconName = 'emoji-1F4A0'
// put the background color code or '' to disable
def jumpInMapBackgroundColorCode = '#003333'

def node = ScriptUtils.node()
if (!node.isRoot()) {
    MenuUtils.executeMenuItems(['JumpInAction'])
    // add the style to mind map, if not exists
    def mapModel = (node.mindMap as MapProxy).delegate
    def mapStyleModel = MapStyleModel.getExtension(mapModel)
    def userStyleParentNode = mapStyleModel.getStyleNodeGroup(mapStyleModel.styleMap, STYLES_USER_DEFINED)
    def jumpInStyleFound = userStyleParentNode.children.find { it.text == jumpInStyleName }
    if (!jumpInStyleFound) {
        def styleNodeModel = new NodeModel(mapModel)
        def iStyle = StyleFactory.create(jumpInStyleName)
        styleNodeModel.setUserObject(iStyle)
        userStyleParentNode.insert(styleNodeModel, userStyleParentNode.childCount) // no event triggered
        mapStyleModel.addStyleNode(styleNodeModel)
        if (jumpInIconName) {
            def jumpInIcon = IconStoreFactory.ICON_STORE.getMindIcon(jumpInIconName)
            styleNodeModel.addIcon(jumpInIcon)
        }
    }
    def styleAlreadyAdded = node.conditionalStyles.find { it.active && it.always && it.styleName == jumpInStyleName && !it.last }
    if (!styleAlreadyAdded) {
        node.conditionalStyles.insert(0, true, null, jumpInStyleName, false)
    }
    if (jumpInMapBackgroundColorCode) {
        // save the original color to restore it on jump out
        ScriptUtils.c().viewRoot['jumpInBackgroundColor'] = node.mindMap.backgroundColorCode ?: 'null'
        node.mindMap.backgroundColorCode = jumpInMapBackgroundColorCode
    }
    if (FreeplaneVersion.version < new FreeplaneVersion(1, 11, 1)) {
        // force view refresh
        node.style.name = node.style.name
    }
}
