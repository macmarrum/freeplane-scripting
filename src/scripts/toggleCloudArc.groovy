/*
 * Copyright (C) 2023, 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Toggle"})
import org.freeplane.api.Node as FN

// enable bright ARC cloud
// or change to ARC, keeping the original color
// else disable

c.selecteds.each { FN node ->
    def COLOR = '#c7c7c7'
    def SHAPE = 'ARC'
    def cloud = node.cloud
    if (!cloud.enabled) {
        cloud.colorCode = COLOR
        cloud.shape = SHAPE
    } else { // cloud is enabled
        if (cloud.shape != SHAPE)
            cloud.shape = SHAPE
        else
            cloud.enabled = false
    }
}
