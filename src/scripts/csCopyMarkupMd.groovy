// Copyright (C) 2022-2024  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/CStorage"})


import io.github.macmarrum.freeplane.ConfluenceStorage
import org.freeplane.api.Node

node = node as Node

ConfluenceStorage.flavor = ConfluenceStorage.Flavor.MD
ConfluenceStorage.copyMarkup(node)
