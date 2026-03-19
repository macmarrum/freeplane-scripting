/*
 * Copyright (C) 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Format"})


import io.github.macmarrum.freeplane.WidthUtils
import org.freeplane.api.Controller
import org.freeplane.api.Node

c = c as Controller
c.selecteds.each { Node n ->
    formatAsTable(n)
}

static void formatAsTable(Node node, boolean withHeader = true) {
    def listOfColumns = WidthUtils.createListOfColumns(node)
    WidthUtils.alignToMaxWidthInEachColumn(listOfColumns)
    listOfColumns.eachWithIndex { columnOfNodes, colIdx ->
        if (colIdx == 0) {
            columnOfNodes.eachWithIndex { Node n, int rowIdx ->
                if (rowIdx == 0) n.style.name = withHeader ? '=Table.row.accent' : 'Table.row'
                else n.style.name = '=Table.row'
            }
        } else {
            columnOfNodes.eachWithIndex { Node n, int i ->
                if (i == 0) n.style.name = withHeader ? '=Table.cell.accent' : '=Table.cell'
                else n.style.name = '=Table.cell'
            }
        }
    }
    node.style.name = '=Table'
}
