/*
 * Copyright (C) 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Paste"})

import org.freeplane.api.Node
import org.freeplane.core.util.MenuUtils

node = node as Node
def nodesBefore = node.children.collect() as List<Node>
MenuUtils.executeMenuItems(['PasteAction'])
(node.children - nodesBefore).each { Node n ->
    def file = n.link.file
    if (file.name.find(/\.(png|jpg|jpeg|gif|apng|webp)$/)) {  // apng, webp not supported in Java yet
        n.externalObject.file = file
    }
}
