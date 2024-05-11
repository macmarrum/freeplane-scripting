// Copyright (C) 2024  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Tags"})
import io.github.macmarrum.freeplane.TagsUtils
import org.freeplane.api.Node

c.selecteds.each { Node node ->
    def preBranchTagsJson = node[TagsUtils.preBranchTags].text
    if (preBranchTagsJson) {
        TagsUtils.hideBranchTags(node, preBranchTagsJson)
    } else {
        TagsUtils.showBranchTags(node)
    }
}
