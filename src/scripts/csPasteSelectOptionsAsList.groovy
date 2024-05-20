// Copyright (C) 2024  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/CStorage"})


import groovy.xml.XmlParser
import groovy.xml.XmlUtil
import io.github.macmarrum.freeplane.ConfluenceStorage
import org.freeplane.plugin.script.proxy.ScriptUtils

import java.awt.*
import java.awt.datatransfer.DataFlavor

def c = ScriptUtils.c()
def node = ScriptUtils.node()
def transferable = Toolkit.defaultToolkit.systemClipboard.getContents(null)
String text
if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
    text = transferable.getTransferData(DataFlavor.stringFlavor)
}
if (text) {
    def select = new XmlParser(false, false).parseText(text)
    if (select.name() != 'select')
        c.statusInfo = "csPasteSelectOptionsAsList: expected xml in clipboard with root <select> - found ${select.name()}"
    else {
        def lst = ConfluenceStorage.createList(node)
        select.children().each { groovy.util.Node it ->
            def t = it.text().strip()
            def n = lst.createChild(t)
            if (XmlUtil.escapeXml(t) != t)
                n.icons.add(ConfluenceStorage.icon.xmlEscape_broom)
        }
    }
}
