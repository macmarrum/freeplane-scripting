/*
 * Copyright (C) 2022, 2024, 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Width"})


import io.github.macmarrum.freeplane.WidthUtils
import org.freeplane.api.Controller
import org.freeplane.api.Node

c = c as Controller

def visibleSinglesAndCloneLeadersOfSelected = WidthUtils.findSinglesAndCloneLeaders(c.selecteds*.children.flatten() as Iterable<Node>).findAll { it.isVisible() }
WidthUtils.alignToMaxWidthInCollection(visibleSinglesAndCloneLeadersOfSelected)
