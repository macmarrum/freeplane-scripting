// Copyright (C) 2024  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/CStorage"})


import groovy.xml.XmlUtil
import io.github.macmarrum.freeplane.ConfluenceStorage
import org.freeplane.api.Controller
import org.freeplane.api.Node

import java.awt.*
import java.awt.datatransfer.DataFlavor

c = c as Controller
node = node as Node

def transferable = Toolkit.defaultToolkit.systemClipboard.getContents(null)
String text
if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
    text = transferable.getTransferData(DataFlavor.stringFlavor)
}
if (text) {
    def (tableNode, numberNode) = ConfluenceStorage.createTable(node)
    tableNode[ConfluenceStorage.HILITE1ST] = ConfluenceStorage.HiLite1st.ROW
    def listOfLines = text.split('\n')
    listOfLines.eachWithIndex { ln, i ->
        def cellNode = i > 0 ? tableNode.appendChild(numberNode) : numberNode
        // use tab as a cell delimiter (standard for content copied from a spreadsheet)
        // use limit: -1 to keep trailing empty strings
        ln.split('\t', -1).each { cellText ->
            cellNode = cellNode.createChild(cellText)
            if (XmlUtil.escapeXml(cellText) != cellText)
                cellNode.icons.add(ConfluenceStorage.icon.xmlEscape_broom)
        }
    }
    c.select(tableNode)
}
