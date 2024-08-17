/**
 * Copyright (C) 2022, 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Select"})


import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.freeplane.features.explorer.NodeAlias
import org.freeplane.features.explorer.NodeAliases
import org.freeplane.features.mode.Controller

import javax.swing.*

final c = c as org.freeplane.api.Controller

final Controller controller = Controller.currentController
final Collection<NodeAlias> nodeAliases = NodeAliases.of(controller.map).aliases()
final String[] aliases = nodeAliases.collect(new TreeSet<String>()) { it.value }.toArray(new String[0])
String input = JOptionPane.showInputDialog(UITools.currentRootComponent, null, "Go to alias",
        JOptionPane.QUESTION_MESSAGE, null, aliases, null)
if (input == null || input == '') {
    c.statusInfo = 'input is null or blank'
    return
} else {
    // find and select all nodes with this alias
    List<Node> toBeSelected = c.find { Node it -> it.alias == input }
    if (toBeSelected)
        c.select(toBeSelected)
    else
        c.statusInfo = "$input not found"
}
