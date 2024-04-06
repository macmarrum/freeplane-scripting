/*
 * Copyright (C) 2023, 2024  macmarrum (at) outlook (dot) ie
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Jump"})


import org.freeplane.core.util.FreeplaneVersion
import org.freeplane.core.util.MenuUtils
import org.freeplane.features.icon.factory.IconStoreFactory
import org.freeplane.features.map.NodeModel
import org.freeplane.features.styles.MapStyleModel
import org.freeplane.features.styles.StyleFactory
import org.freeplane.features.styles.StyleTranslatedObject
import org.freeplane.plugin.script.proxy.MapProxy
import org.freeplane.plugin.script.proxy.NodeProxy
import org.freeplane.plugin.script.proxy.ScriptUtils

import static org.freeplane.features.styles.MapStyleModel.STYLES_AUTOMATIC_LAYOUT
import static org.freeplane.features.styles.MapStyleModel.STYLES_USER_DEFINED

//import static org.freeplane.features.styles.AutomaticLayoutController.AUTOMATIC_LAYOUT_LEVEL_ROOT
// AutomaticLayoutController.AUTOMATIC_LAYOUT_LEVEL_ROOT is private
final AUTOMATIC_LAYOUT_LEVEL_ROOT = 'AutomaticLayout.level.root'

// specify the name of your custom style - it will be created if it doesn't exist
def jumpInStyleName = 'JumpIn'
// set to true for JumpIn to take on Auto-level-Root style
def shouldJumpInDeriveFromAutoLevelRoot = true
// specify the background color code or '' to disable
def jumpInMapBackgroundColorCode = '#003333'

String jumpInIconName
if (FreeplaneVersion.version < new FreeplaneVersion(1, 11, 7)) {
    // for version before 1.11.7, which doesn't yet display a pin for jump-in
    // specify the name of the icon or '' to skip icon
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
}
