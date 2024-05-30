/** Moves the current Style to the end of Node Conditional Styles
 * Copyright (C) 2022-2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/NodeCondies"})


import org.freeplane.api.Controller
import org.freeplane.api.Node

def c = c as Controller

String styleName
for (Node selectedNode in c.selecteds) {
    styleName = selectedNode.style.name
    if (styleName === null)
        continue
    selectedNode.conditionalStyles.add(true, null, styleName, false)
    selectedNode.style.name = null
}
