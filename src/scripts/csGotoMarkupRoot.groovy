// Copyright (C) 2024  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/CStorage"})
import io.github.macmarrum.freeplane.ConfluenceStorage
import org.freeplane.api.Controller
import org.freeplane.api.Node

def node = node as Node
def c = c as Controller

def target = node.pathToRoot.reverse().drop(1).find { it.style.name == ConfluenceStorage.style.cStorageMarkupRoot }
if (target)
    c.select(target)
else
    c.select(node.mindMap.root)
