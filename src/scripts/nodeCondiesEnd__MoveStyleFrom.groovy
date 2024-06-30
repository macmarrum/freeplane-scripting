/** Removes the last style from Node Conditional Styles and sets it as the current Style
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
List<ConditionalStyle> nodeCondies
ConditionalStyle nodeCondi
int i
findSinglesAndCloneLeaders(c.selecteds).each { Node selectedNode ->
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
