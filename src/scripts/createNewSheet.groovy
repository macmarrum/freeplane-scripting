/*
 * Copyright (C) 2025  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Create"})


import org.freeplane.api.Node
import org.freeplane.core.util.MenuUtils

node = node as Node
def root = node.mindMap.root
// work out the number (name) of the new Sheet
def rxSheetNum = ~/^Sheet(\d+).*/
int maxNum = 0
int num
for (child in root.children) {
    def matcher = child.text =~ rxSheetNum
    while (matcher.find()) {
        num = matcher.group(1) as int
        maxNum = Math.max(maxNum, num)
    }
}
def name = "Sheet${maxNum + 1}" as String
// create it
def sheet = root.createChild(name)
sheet.id // after 1.12.12-pre08 it won't be needed - fixed in d87b619f
sheet.setBookmark(name, 'ROOT')
sheet.bookmark.open()
MenuUtils.executeMenuItems(['NodeEnumerationAction.NodeVisibility.HIDDEN'])
