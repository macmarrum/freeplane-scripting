// @ExecutionModes({ON_SELECTED_NODE="/menu_bar/Mac2"})
/*
 *  Copyright (C) 2011  dimitry
 *  Copyright (C) 2023  macmarrum
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
import org.freeplane.api.Node
import org.freeplane.core.util.MenuUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode


// Based on org.freeplane.features.help.HotKeyInfoAction
void appendHotKeyNodes(Node root, Enumeration<TreeNode> children, String title, int level) {
    def menuEntries = new ArrayList<MenuUtils.MenuEntry>()
    def submenus = new ArrayList<DefaultMutableTreeNode>()
    // sort and divide
    while (children.hasMoreElements()) {
        def treeNode = children.nextElement() as DefaultMutableTreeNode
        if (treeNode.isLeaf()) {
            menuEntries.add(treeNode.userObject as MenuUtils.MenuEntry)
        } else {
            submenus.add(treeNode)
        }
    }
    // actions
    def node = title ? root.createChild(title) : root
    if (!menuEntries.isEmpty()) {
        menuEntries.each { MenuUtils.MenuEntry entry ->
            def keystroke = entry.keyStroke == null ? "" : MenuUtils.formatKeyStroke(entry.keyStroke)
            def n = node.createChild(entry.label)
            n.createChild(keystroke)
            n.note = entry.toolTipText
        }
    }
    // submenus
    submenus.each { DefaultMutableTreeNode treeNode ->
        def subtitle = (level > 2 ? title + ' -> ' : '') + treeNode.userObject
        appendHotKeyNodes(root, treeNode.children(), subtitle, level + 1)
    }
}

MenuUtils.executeMenuItems(['NewMapAction'])
def hotKeyRoot = ScriptUtils.node().mindMap.root.createChild('Hot Keys')
def menuEntryTree = MenuUtils.createAcceleratebleMenuEntryTree('main_menu')
appendHotKeyNodes(hotKeyRoot, menuEntryTree.children(), null, 2)
