/*
 * Copyright (C) 2025  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Insert"})

import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.util.MenuUtils
import static org.freeplane.plugin.script.GroovyStaticImports.config

/* Freeplane always activates an editor for new nodes. This script never does - Scripting API limitations.
 * https://github.com/freeplane/freeplane/issues/2563
 * Cloud is copied alongside node's format (part of FormatCopy/FormatPaste)
 * If copyFormatToNewNodeIncludesIcons, icons are copied alongside node's format (part of FormatCopy/FormatPaste)
 */
c = c as Controller
def copyFormatToNewSibling = config.getBooleanProperty("copyFormatToNewSibling")
def toBeSelected = new LinkedList<Node>()
def viewRoot = c.viewRoot
for (selected in c.selecteds.collect()) {
    def parent = selected.parent
    def selectedPosition = parent.getChildPosition(selected)
    def newNodePosition = selectedPosition
    def newNode = parent.createChild(newNodePosition)
    if (parent == viewRoot)
        newNode.left = selected.left
    if (copyFormatToNewSibling) {
        c.select(selected)
        MenuUtils.executeMenuItems(['FormatCopy'])
        c.select(newNode)
        MenuUtils.executeMenuItems(['FormatPaste'])
    }
    toBeSelected.add(newNode)
}
c.select(toBeSelected)
