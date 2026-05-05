/*
 * Copyright (C) 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Create"})


import org.freeplane.api.Node
import org.freeplane.api.Controller

node = node as Node
c = c as Controller
def today = (new Date()).format('yyyy-MM-dd')
def position = node.parent.getChildPosition(node) + 1
def n = node.parent.createChild(position)
n.text = today
c.select(n.createChild())
