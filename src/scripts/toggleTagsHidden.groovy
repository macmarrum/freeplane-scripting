/*
 * Copyright (C) 2025  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Tags"})
import io.github.macmarrum.freeplane.TagsUtils
import org.freeplane.api.Controller
import org.freeplane.api.Node

c = c as Controller
c.selecteds.each { Node node ->
    def preStashTagsJson = node[TagsUtils.preStashTags].text
    if (preStashTagsJson) {
        TagsUtils.unstashNodeTags(node, preStashTagsJson)
    } else {
        TagsUtils.stashNodeTags(node)
    }
}
