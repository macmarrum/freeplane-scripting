// Copyright (C) 2024  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
import org.freeplane.api.Node

String alias
Node aliasNode
Node root = node.mindMap.root
Node aliasesRoot = root.createChild("Aliases ${format(new Date())}")
root.findAll().each { Node node ->
    alias = node.alias
    if (alias) {
        aliasNode = aliasesRoot.createChild(alias)
        if (node.isGlobal)
            aliasNode.icons.add('bookmark')
        if (node.isEncrypted()) {
            aliasNode.appendChild(node).link.node = node
            aliasNode.icons.add('password')
        } else {
            aliasNode.appendAsCloneWithoutSubtree(node)
        }
        aliasNode.folded = true
    }
}
