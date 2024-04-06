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
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})

import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.freeplane.features.map.NodeModel
import org.freeplane.plugin.script.proxy.ScriptUtils

import javax.swing.*

final c = ScriptUtils.c()
final nodeModel = c.selected.delegate as NodeModel
final regexPattern = UITools.showInputDialog(nodeModel, 'NB Special characters <([{\\^-=$!|]})?*+.>', 'Split On Regex', JOptionPane.QUESTION_MESSAGE)
if (regexPattern !in [null, '']) {
    c.selecteds.each { Node nodeToBeSplit ->
        def newlyCreatedChild = nodeToBeSplit  // the initial one is the original node
        def children = nodeToBeSplit.children
        nodeToBeSplit.text.split(regexPattern).each {
            newlyCreatedChild = nodeToBeSplit.createChild(it.trim())
            newlyCreatedChild.style.name = nodeToBeSplit.style.name
            nodeToBeSplit.conditionalStyles.each {
                newlyCreatedChild.conditionalStyles.add(it)
            }
        }
        children.each { it.moveTo(newlyCreatedChild) }
    }
}
