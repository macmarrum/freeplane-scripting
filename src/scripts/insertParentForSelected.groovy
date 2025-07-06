/*
 * Copyright (C) 2021-2023,2025  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Insert"})

import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.util.MenuUtils

import static org.freeplane.plugin.script.GroovyStaticImports.config

/*
 * If copyFormatToNewChild, format is copied from the parent
 * else if copyFormatToNewSibling, format is copied from the selected node (child-to-be)
 * Cloud is copied alongside the format (part of FormatCopy/FormatPaste)
 * No format is copied if both options are off
 * If copyFormatToNewNodeIncludesIcons, icons are copied alongside the format (part of FormatCopy/FormatPaste)
 */
c = c as Controller
boolean copyFormatToNewChild = config.getBooleanProperty("copyFormatToNewChild")
boolean copyFormatToNewSibling = config.getBooleanProperty("copyFormatToNewSibling")
Node parent
int position
Node newParent
Node source
def selectedToParentAndPosition = new HashMap<Node, List<Object>>()
for (selected in c.selecteds) {
    parent = selected.parent
    selectedToParentAndPosition[selected] = [parent, parent.getChildPosition(selected)]
}
def toBeSelected = new HashSet<Node>()

Node selected
for (entry in selectedToParentAndPosition.entrySet()) {
    selected = entry.key
    parent = entry.value[0] as Node
    position = entry.value[1] as int
    newParent = parent.createChild(position)
    if (parent.isRoot())
        newParent.left = selected.left
    selected.moveTo(newParent)
    if (copyFormatToNewChild || copyFormatToNewSibling) {
        source = copyFormatToNewChild ? parent : copyFormatToNewSibling ? selected : null
        c.select(source)
        MenuUtils.executeMenuItems(['FormatCopy'])
        c.select(newParent)
        MenuUtils.executeMenuItems(['FormatPaste'])
    }
    toBeSelected.add(newParent)
}
c.select(toBeSelected)
