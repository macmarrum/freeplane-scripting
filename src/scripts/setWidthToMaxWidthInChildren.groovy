/*
 * Copyright (C) 2022, 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Width"})

import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.features.map.NodeModel

def c = c as Controller

static findSinglesAndCloneLeaders(Iterable<Node> nodes) {
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

static int getWidth(Node node, float zoom) {
    def m = node.delegate as NodeModel
    def v = m.viewers
    def viewCount = v.size()
    assert viewCount == 1, "Only one view is supported, got ${viewCount}"
    // add 1% as a buffer, because nodes and fonts scale differently, i.e.
    // text in fixed-size nodes gets wrapped when monitor size changes (monitor_size_inches decrease)
    double width = v[0].mainView.width / zoom * 1.01
    return Math.ceil(width)
}

def visibleSinglesAndCloneLeadersOfSelected = findSinglesAndCloneLeaders(c.selecteds*.children.flatten() as Iterable<Node>).findAll { it.isVisible() }

float zoom = c.getZoom()
int maxWidthInSelection = 0
visibleSinglesAndCloneLeadersOfSelected.each { Node n ->
    int w = getWidth(n, zoom)
    if (w > maxWidthInSelection)
        maxWidthInSelection = w
}

visibleSinglesAndCloneLeadersOfSelected.each { Node n ->
    n.style.maxNodeWidth = maxWidthInSelection
    n.style.minNodeWidth = maxWidthInSelection
}
