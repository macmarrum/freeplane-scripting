/*
 * Copyright (C) 2022, 2024, 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Width"})


import io.github.macmarrum.freeplane.WidthUtils
import org.freeplane.api.Controller

def c = c as Controller

def visibleSinglesAndCloneLeadersOfSelected = WidthUtils.findSinglesAndCloneLeaders(c.selecteds)
WidthUtils.resetWidth(visibleSinglesAndCloneLeadersOfSelected)
