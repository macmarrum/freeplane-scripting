/** Removes the first style from Node Conditional Styles and sets it as the current Style
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
Iterator<ConditionalStyle> iterator
int i
ConditionalStyle nodeCondi
c.selecteds.each { Node selectedNode ->
    conditionalStyles = selectedNode.conditionalStyles
    iterator = conditionalStyles.iterator()
    i = 0
    while (iterator.hasNext()) {
        nodeCondi = iterator.next()
        if (nodeCondi.active && nodeCondi.always && !nodeCondi.last) {
            // not using iterator.remove() as it doesn't callDelayedRefresh(...)
            conditionalStyles.remove(i)
            break
        }
        i++
    }
}
