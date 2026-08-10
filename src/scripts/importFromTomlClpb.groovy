/**
 * Copyright (C) 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Import"})

import groovy.toml.TomlSlurper
import io.github.macmarrum.freeplane.Import
import org.freeplane.api.Controller
import org.freeplane.api.Node

import java.awt.*
import java.awt.datatransfer.DataFlavor

c = c as Controller
def transferable = Toolkit.defaultToolkit.systemClipboard.getContents(null)
String text
if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
    text = transferable.getTransferData(DataFlavor.stringFlavor)
}
if (text) {
    def jObject = new TomlSlurper().parseText(text)
    c.selecteds.each { Node n -> Import.fromJsonObject(jObject, n) }
} else {
    c.statusInfo = 'importFromJsonClpb: no text in clipboard'
}
