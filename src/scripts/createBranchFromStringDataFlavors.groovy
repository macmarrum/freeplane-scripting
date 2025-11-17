/*
 * Copyright (C) 2025  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Create"})

import org.freeplane.api.Controller

import java.awt.Toolkit

c = c as Controller

def cb = Toolkit.defaultToolkit.systemClipboard
def t = cb.getContents(null)

def branch = c.selected.createChild((new Date()).format('yyyy-MM-dd HH:mm:ss'))
c.select(branch)
for (df in t.transferDataFlavors) {
    if (df.representationClass.simpleName == 'String') {
        def child = branch.createChild(df.mimeType).createChild()
        try {
            child.text = t.getTransferData(df).toString()
        } catch (Exception e) {
            child.details = e.message
        }
    }
}
