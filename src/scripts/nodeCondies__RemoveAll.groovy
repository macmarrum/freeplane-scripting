/**
 * Copyright (C) 2022, 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/NodeCondies"})


import org.freeplane.api.Controller
import org.freeplane.api.Node

def c = c as Controller
c.selecteds.each { Node n ->
    n.conditionalStyles.collect().each { it.remove() }
}
