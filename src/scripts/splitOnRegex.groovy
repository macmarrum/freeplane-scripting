// Copyright (C) 2022, 2024, 2025  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})


import io.github.macmarrum.swing.ComboBoxDialog
import io.github.macmarrum.swing.ComboBoxEnricher
import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools

import javax.swing.*

c = c as Controller
def title = 'Split On Regex'
def checkboxText = 'V&ertically'
def message = 'NB Special characters <([{\\^-=$!|]})?*+.>'
def regexPatterns = [
        $/\s+(?=([^"'\[]*["'\[][^"'\]]*["'\]])*[^"'\[\]]*$)/$,
        /\n+/,
        /,\s*/,
        /\s+/,
]

def onEntryAccepted = { JComboBox<String> comboBox, JCheckBox checkBox ->
    def regexPattern = comboBox.editor.item as String
    def isVertically = checkBox.isSelected()
    c.selecteds.each { Node nodeToBeSplit ->
        def newlyCreatedChild = nodeToBeSplit
        def children = nodeToBeSplit.children
        def textSegments = nodeToBeSplit.text.split(regexPattern)
        if (textSegments.size() > 1) {
            for (textSegment in textSegments) {
                def nodeForCreateChild = isVertically ? nodeToBeSplit : newlyCreatedChild
                newlyCreatedChild = nodeForCreateChild.createChild(textSegment.trim())
                newlyCreatedChild.style.name = nodeToBeSplit.style.name
                nodeToBeSplit.conditionalStyles.each {
                    newlyCreatedChild.conditionalStyles.add(it)
                }
            }
            nodeToBeSplit.text = null
            for (child in children) {
                child.moveTo(newlyCreatedChild)
            }
        }
    }
}

def onCheckboxToggled = onEntryAccepted

SwingUtilities.invokeLater {
    new ComboBoxDialog(UITools.currentFrame, ComboBoxEnricher, title, regexPatterns as String[], onEntryAccepted, message, checkboxText, onCheckboxToggled)
}
