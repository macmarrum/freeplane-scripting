/**
 * Copyright (C) 2024 - 2025  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Import"})
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
    c.selecteds.each { Node n -> Import.fromJsonString(text, n) }
} else {
    c.statusInfo = 'importFromJsonClpb: no text in clipboard'
}
