/*
 * Copyright (C) 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Convert"})
import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.features.link.LinkController

c = c as Controller
node = node as Node

def mindMapFile = node.mindMap.file

c.findAll().each { Node n ->
    String uriStr = n.externalObject?.uri
    if (uriStr) {
        URI uri = uriStr.toURI()
        def file = new File(uri)
        if (file.isAbsolute()) {
            n.externalObject.uri = LinkController.toLinkTypeDependantURI(mindMapFile, file, LinkController.LINK_RELATIVE_TO_MINDMAP)
        }
    }
}
