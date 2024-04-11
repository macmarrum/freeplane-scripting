/*
 * Copyright (C) 2024  macmarrum (at) outlook (dot) ie
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Paste"})


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
    def (table, elem) = ConfluenceStorage.createTable(node)
    table[ConfluenceStorage.HILITE1ST] = ConfluenceStorage.HiLite1st.ROW
    def listOfLines = text.split('\n')
    listOfLines.eachWithIndex { ln, i ->
        if (i > 0)
            elem = table.appendChild(elem)
        def cell = elem
        // use tab as a cell delimiter (standard for content copied from a spreadsheet)
        ln.split('\t').each {
            cell = cell.createChild(it)
        }
    }
    c.select(table)
}
