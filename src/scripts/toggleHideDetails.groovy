/*
 * Copyright (C) 2022, 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Toggle"})
import org.freeplane.plugin.script.proxy.ScriptUtils

ScriptUtils.c().selecteds.each { it.hideDetails = !it.hideDetails }
