/*
 * Copyright (C) 2025  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Import"})


import io.github.macmarrum.freeplane.Import
import org.freeplane.api.Node

node = node as Node

def suggestedFile = new File(node.mindMap.file.parent, 'freeplane.json')
def file = Import.askForFile(suggestedFile)
Import.fromJsonFile(file, node, false)
