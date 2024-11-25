/**
 * Copyright (C) 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Create"})


import org.freeplane.api.Controller
import org.freeplane.api.Node

import java.awt.*
import java.awt.datatransfer.DataFlavor

node = node as Node
c = c as Controller


LinkedList<String> mimeTypeParts
String mimeType
def mimeTypeToDataFlavors = new LinkedHashMap<String, LinkedList<DataFlavor>>()
def t = Toolkit.defaultToolkit.systemClipboard.getContents(null)
t.transferDataFlavors.each {
    mimeTypeParts = it.mimeType.split(';').collect { it.strip() }
    mimeType = mimeTypeParts[0]
    if (!mimeTypeToDataFlavors.containsKey(mimeType))
        mimeTypeToDataFlavors[mimeType] = []
    mimeTypeToDataFlavors[mimeType] << it
}

Node mtNode
Node rClassNode
String reprClass
Node descrNode
String descr
Node valueNode
def reprClassToDataFlavors = new LinkedHashMap<String, LinkedList<DataFlavor>>()

def n = node.createChild((new Date()).format('yyyy-MM-dd HH:mm:ss'))
mimeTypeToDataFlavors.each { String mt, dfList ->
    reprClassToDataFlavors.clear()
    mtNode = n.createChild(mt)
    dfList.each {
        mimeTypeParts = it.mimeType.split(';').collect { it.strip() }
        reprClass = mimeTypeParts.find { it.startsWith('class=') }
        if (!reprClassToDataFlavors.containsKey(reprClass))
            reprClassToDataFlavors[reprClass] = []
        reprClassToDataFlavors[reprClass] << it
    }
    reprClassToDataFlavors.each { rc, dtLst ->
        rClassNode = mtNode.createChild(rc)
        dtLst.each { DataFlavor it ->
            descr = it.mimeType.split(';').drop(1).collect { it.strip() }.findAll { !it.startsWith('class=') }.join('; ')
            descrNode = rClassNode.createChild(descr)
            valueNode = descrNode.createChild(t.getTransferData(it).toString())
            valueNode.style.name = '=Code'
            descrNode.folded = true
        }
    }
}
c.select(n)
