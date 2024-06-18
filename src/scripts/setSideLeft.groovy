/*
 * Copyright (C) 2022, 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac3"})

import org.freeplane.api.Controller
import org.freeplane.api.Side

def c = c as Controller
Side left
try {
    left = Side.TOP_OR_LEFT
} catch (MissingPropertyException ignored) {
    left = Side.LEFT
}
c.selecteds.each { it.sideAtRoot = left }
