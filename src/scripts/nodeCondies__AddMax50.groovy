/** Adds +max??cm to node condies and removes any other +max from node condies
 * Copyright (C) 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SELECTED_NODE="/menu_bar/Mac1/NodeCondies"})

import org.freeplane.api.ConditionalStyle
import org.freeplane.api.Controller

def STYLE_NAME = '+max50cm'

def c = c as Controller
c.selected.each { n ->
    def ncs = n.conditionalStyles
    def isFound = false
    def indexesToBeRemoved = new ArrayList<Integer>()
    int i = 0
    for (ConditionalStyle condi in ncs) {
        if (condi.active && condi.always && condi.styleName == STYLE_NAME && !condi.last) {
            isFound = true
        } else if (condi.active && condi.always && condi.styleName.startsWith('+max') && !condi.last) {
            indexesToBeRemoved << i
        }
        i++
    }
    indexesToBeRemoved.reverseEach {
        ncs.remove(it)
    }
    if (!isFound) {
        ncs.add(true, null, STYLE_NAME, false)
    }
}
