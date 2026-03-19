/*
 * Copyright (C) 2022, 2024, 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package io.github.macmarrum.freeplane

import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.features.map.NodeModel
import org.freeplane.plugin.script.proxy.ScriptUtils
import org.freeplane.view.swing.map.NodeView


class WidthUtils {
    private static Controller c = ScriptUtils.c()

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
        def v = m.viewers.findAll { it instanceof NodeView }
        def viewCount = v.size()
        assert viewCount == 1, "Only one view is supported, got ${viewCount}"
        // add 1% as a buffer, because nodes and fonts scale differently, i.e.
        // text in fixed-size nodes gets wrapped when monitor size changes (monitor_size_inches decrease)
        double width = v[0].mainView.width / zoom * 1.01
        return Math.ceil(width)
    }

    static void alignToMaxWidthInCollection(Collection<Node> visibleSinglesAndCloneLeaders) {
        float zoom = c.getZoom()
        int maxWidthInSelection = 0
        visibleSinglesAndCloneLeaders.each { Node it ->
            int w = getWidth(it, zoom)
            if (w > maxWidthInSelection)
                maxWidthInSelection = w
        }
        visibleSinglesAndCloneLeaders.each { Node it ->
            it.style.maxNodeWidth = maxWidthInSelection
            it.style.minNodeWidth = maxWidthInSelection
        }
    }

    /** Restore default node width (min, max) */
    static void resetWidth(Collection<Node> visibleSinglesAndCloneLeaders) {
        visibleSinglesAndCloneLeaders.each { Node n ->
            n.style.maxNodeWidth = -1
            n.style.minNodeWidth = -1
        }
    }

    static void alignToMaxWidthInEachColumn(List<List<Node>> listOfColumns) {
        listOfColumns.each { columnOfNodes ->
            def visibleSinglesAndCloneLeaders = findSinglesAndCloneLeaders(columnOfNodes).findAll { it.isVisible() }
            alignToMaxWidthInCollection(visibleSinglesAndCloneLeaders)
        }
    }

    static List<List<Node>> createListOfColumns(Node topNode) {
        def excludingHidden = false  // countsHidden
        TreeMap<Integer, List<Node>> levelToNodes = new TreeMap<>()
        topNode.find { it.id != topNode.id }.each { Node n ->
            levelToNodes.compute(n.getNodeLevel(excludingHidden)) { k, v ->
                if (v == null) v = new LinkedList<Node>()
                v << n
                return v
            }
        }
        return levelToNodes.collect { level, nodeList -> nodeList }
    }
}
