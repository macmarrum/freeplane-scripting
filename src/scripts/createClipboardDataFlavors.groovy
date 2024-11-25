/**
 * Copyright (C) 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Create"})


import org.freeplane.api.Controller
import org.freeplane.api.Node

import java.awt.*

node = node as Node
c = c as Controller

def t = Toolkit.defaultToolkit.systemClipboard.getContents(null)

def n = node.createChild((new Date()).format('yyyy-MM-dd HH:mm:ss'))
t.transferDataFlavors.each {
    def child = n.createChild(it.mimeType.replace('; ', '  |  '))
    child.createChild().text = t.getTransferData(it).toString()
    child.folded = true
    child.style.name = '+max20cm'
}
c.select(n)
