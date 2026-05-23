/*
 * Copyright (C) 2022, 2023, 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Toggle"})
import org.freeplane.api.Controller

c = c as Controller
c.selecteds.each { node -> node.cloud.enabled = !node.cloud.enabled }
