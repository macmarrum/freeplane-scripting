/** Removes the first style from Node Conditional Styles and sets it as the current Style
 * Copyright (C) 2022-2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/NodeCondies"})


import org.freeplane.api.ConditionalStyle
import org.freeplane.api.ConditionalStyles
import org.freeplane.api.Controller
import org.freeplane.api.Node

static findSinglesAndCloneLeaders(Iterable<Node> nodes) {
    if (nodes.size() == 1)
        return nodes
    def singlesAndLeaders = new LinkedList<Node>()
    def subtrees = new HashSet<Node>()
    def clones = new HashSet<Node>()
    nodes.each { Node n ->
        if (n !in subtrees) { // not a tree clone or a clone leader
            def nodesSharingContentAndSubtree = n.nodesSharingContentAndSubtree
            subtrees.addAll(nodesSharingContentAndSubtree)
            if (n !in clones) { // not a content clone or a clone leader
                singlesAndLeaders << n
                clones.addAll(n.nodesSharingContent - nodesSharingContentAndSubtree)
            }
        }
    }
    return singlesAndLeaders
}

def c = c as Controller
ConditionalStyles conditionalStyles
Iterator<ConditionalStyle> iterator
int i
ConditionalStyle nodeCondi
findSinglesAndCloneLeaders(c.selecteds).each { Node selectedNode ->
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
