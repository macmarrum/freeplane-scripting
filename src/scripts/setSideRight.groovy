/*
 * Copyright (C) 2022, 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac3"})

import org.freeplane.api.Controller
import org.freeplane.api.Side

def c = c as Controller
Side right
try {
    right = Side.BOTTOM_OR_RIGHT
} catch (MissingPropertyException ignored) {
    right = Side.RIGHT
}
c.selecteds.each { it.sideAtRoot = right }
