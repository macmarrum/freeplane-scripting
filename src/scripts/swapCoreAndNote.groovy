// Copyright (C) 2026  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Swap"})

import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.freeplane.features.map.NodeModel

import javax.swing.JOptionPane

c = c as Controller
def nodesWithBoth = []
c.selecteds.each { Node node ->
    if (node.text == '') {
        node.text = node.noteText
        node.note = null
    } else if (node.note == null) {
        node.note = node.text
        node.text = null
    } else {
        nodesWithBoth << node
    }
}
def showWarning = { String message ->
    NodeModel nodeModel = c.selected.delegate
    UITools.showConfirmDialog(nodeModel, message, 'Swap Core and Note', JOptionPane.CLOSED_OPTION, JOptionPane.WARNING_MESSAGE)
}
if (nodesWithBoth) {
    c.select(nodesWithBoth)
    showWarning("${nodesWithBoth.size()} nodes (selected) already have both core and note")
}
