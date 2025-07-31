/*
 * Copyright (C) 2025  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Duplicate"})

import org.freeplane.api.Controller
import org.freeplane.api.Node

c = c as Controller
// differentSubtrees - if true children/grandchildren/grandgrandchildren/... nodes of selected parent nodes are excluded from the result.
def differentSubtrees = true
c.getSortedSelection(differentSubtrees).each { Node n ->
    def parent = n.parent
    def newNode = parent.appendBranch(n)
    newNode.sideAtRoot = n.sideAtRoot
    def nPosition = parent.getChildPosition(n)
    newNode.moveTo(parent, nPosition + 1)
    c.select(newNode)
}
