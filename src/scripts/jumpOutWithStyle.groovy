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


import org.freeplane.api.ConditionalStyle
import org.freeplane.core.util.MenuUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

// specify the name of your custom style - must be the same as in jumpInWithStyle.groovy
def jumpInStyleName = 'JumpIn'

static def clearJumpInStyleAndBackgroundColor(node, jumpInStyleName) {
    def preJumpInBackgroundColorCode = node['preJumpInBackgroundColor']
    if (preJumpInBackgroundColorCode) {
        node.mindMap.backgroundColorCode = preJumpInBackgroundColorCode == 'null' ? null : preJumpInBackgroundColorCode
        node['preJumpInBackgroundColor'] = null
    }
    node.conditionalStyles.findAll {
        it.active && it.always && it.styleName == jumpInStyleName && !it.last
    }.each { ConditionalStyle it ->
        it.remove()
    }
}

def node = ScriptUtils.node()
def c = ScriptUtils.c()
def viewRoot = c.viewRoot
if (viewRoot != node.mindMap.root) {
    clearJumpInStyleAndBackgroundColor(viewRoot, jumpInStyleName)
    MenuUtils.executeMenuItems(['JumpOutAction'])
} else {
    clearJumpInStyleAndBackgroundColor(node, jumpInStyleName)
}
