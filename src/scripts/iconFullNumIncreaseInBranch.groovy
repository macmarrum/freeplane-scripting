/**
 * Copyright (C) 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Icon"})
import org.freeplane.api.Node

def node = node as Node
Integer oldNum = null
List<String> newIcons = null

findRegularsAndCloneFirstOccurrences(node, new LinkedList<Node>(), new LinkedList<Node>(), new LinkedList<Node>()).each { n ->
    def fullIconFound = false
    newIcons = n.icons.icons.collect() {
        if (it.startsWith('full-')) {
            fullIconFound = true
            oldNum = it[5..-1] as Integer
            return oldNum < 9 ? "full-${oldNum + 1}" as String : 'emoji-1F51F' // 10
        } else {
            return it
        }
    }
    if (fullIconFound) {
        n.icons.clear()
        n.icons.addAll(newIcons)
    }
}

def findRegularsAndCloneFirstOccurrences(Node n, List<Node> regularsAndCloneFirstOccurrences, List<Node> ignoreBecauseSubtreeClones, List<Node> ignoreBecauseNonSubtreeClones) {
    //printf('>> findSinglesAndCloneLeaders %s %s ', n.id, n.shortText)
    if (n !in ignoreBecauseSubtreeClones) {
        def subtreeClones = n.nodesSharingContentAndSubtree
        ignoreBecauseSubtreeClones.addAll(subtreeClones)
        if (n !in ignoreBecauseNonSubtreeClones) {
            //printf('- single/leader')
            regularsAndCloneFirstOccurrences << n
            ignoreBecauseNonSubtreeClones.addAll(n.nodesSharingContent - subtreeClones)
        } else {
            //printf('- clone (skipping)')
        }
        def children = n.children
        //println(" - processing children (${children.size()})")
        children.each { Node it ->
            findRegularsAndCloneFirstOccurrences(it, regularsAndCloneFirstOccurrences, ignoreBecauseSubtreeClones, ignoreBecauseNonSubtreeClones)
        }
    } else {
        //println('- subtree clone (skipping it and its children)')
    }
    return regularsAndCloneFirstOccurrences
}
