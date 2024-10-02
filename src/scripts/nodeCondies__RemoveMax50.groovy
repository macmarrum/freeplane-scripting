/**
 * Copyright (C) 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SELECTED_NODE="/menu_bar/Mac1/NodeCondies"})

import org.freeplane.api.Controller

def STYLE_NAME = '+max50cm'

c = c as Controller
c.selected.each { n ->
    def ncs = n.conditionalStyles
    ncs.collect().each {
        if (it.active && it.always && it.styleName == STYLE_NAME && !it.last)
            it.remove()
    }
}
