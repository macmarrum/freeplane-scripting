/*
 * Copyright (C) 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Sort"})
import org.freeplane.api.Controller
import org.freeplane.api.Node

import org.freeplane.features.map.SummaryNode
import org.freeplane.features.map.mindmapmode.MMapController

c = c as Controller
MMapController mapController = c.selected.modeController.mapController
c.selecteds.each { n ->
    def children = n.children.toList()
    def units = new LinkedList<List<Node>>()
    def currentGroup = new LinkedList<Node>()

    children.each { Node child ->
        if (SummaryNode.isFirstGroupNode(child.delegate)) {
            currentGroup = [child]
        } else if (SummaryNode.isSummaryNode(child.delegate)) {
            if (currentGroup) {
                currentGroup << child
                units << currentGroup
                currentGroup = new LinkedList<Node>()
            } else {
                units << [child]
            }
        } else if (currentGroup) {
            currentGroup << child
        } else {
            units << [child]
        }
    }

    // flush any unclosed group at end of children
    if (currentGroup) {
        units << currentGroup
    }

    // Sort the internal nodes of each group unit
    units.each { unit ->
        if (unit.size() > 2 && SummaryNode.isFirstGroupNode(unit[0].delegate)) {
            def first = unit[0]
            def last = unit[-1]
            def internals = unit[1..-2]
            internals.sort { Node it -> it.lastModifiedAt }
            unit.clear()
            unit << first
            unit.addAll(internals)
            unit << last
        } else {
            unit.sort(true) { Node it -> it.lastModifiedAt }
        }
    }

    units.sort(true) { List<Node> unit ->
        if (unit.size() > 1 && SummaryNode.isFirstGroupNode(unit[0].delegate)) {
            def body = unit.size() > 2 ? unit[1..-2] : [unit[0]]
            body*.lastModifiedAt.min()
        } else {
            unit[0].lastModifiedAt
        }
    } as LinkedList<List<Node>>

    int position = 0
    units.each { unit ->
        unitNodes = unit.collect { it.delegate }
        // use mapController.moveNodes because it can move a group of nodes together
        // moving one by one breaks the group (FirstGroupNode .. SummaryNode)
        mapController.moveNodes(unitNodes, n.delegate, position)
        position += unit.size()
    }
}
