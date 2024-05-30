/** Removes the last style from Node Conditional Styles and sets it as the current Style
 * Copyright (C) 2022-2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/NodeCondies"})


import org.freeplane.api.ConditionalStyle
import org.freeplane.api.ConditionalStyles
import org.freeplane.api.Controller
import org.freeplane.api.Node

def c = c as Controller

ConditionalStyles conditionalStyles
List<ConditionalStyle> nodeCondies
ConditionalStyle nodeCondi
int i
c.selecteds.each { Node selectedNode ->
    conditionalStyles = selectedNode.conditionalStyles
    nodeCondies = conditionalStyles.collect()
    for (i = nodeCondies.size() - 1; i >= 0; i--) {
        nodeCondi = nodeCondies[i]
        if (nodeCondi.active && nodeCondi.always & !nodeCondi.last) {
            selectedNode.style.name = nodeCondi.styleName
            conditionalStyles.remove(i)
            break
        }
    }
}
