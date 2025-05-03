/**
 * Copyright (C) 2025  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Import"})
import io.github.macmarrum.freeplane.Import
import org.freeplane.api.Controller
import org.freeplane.api.Node

c = c as Controller
c.selecteds.each { Node n -> Import.fromHtmlTableClipboard(n) }
