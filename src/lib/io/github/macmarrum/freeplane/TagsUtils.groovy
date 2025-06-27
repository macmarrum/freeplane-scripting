/*
 * Copyright (C) 2024-2025  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package io.github.macmarrum.freeplane

import groovy.json.JsonGenerator
import groovy.json.JsonSlurper
import org.freeplane.api.Node

class TagsUtils {
    public static final String preBranchTags = 'preBranchTags'
    public static final String preHideTags = 'preHideTags'
    private static final JsonGenerator jsonGenerator = new JsonGenerator.Options().disableUnicodeEscaping().build()
    private static final JsonSlurper jsonSlurper = new JsonSlurper()

    static void showBranchTags(Node node) {
        def branchTags = new TreeSet<String>()
        node.findAll().each {
            branchTags.addAll(it.tags.tags)
        }
        def nodeTags = node.tags.tags
        if (branchTags != new TreeSet(nodeTags)) {
            node.tags.tags = branchTags
            node[preBranchTags] = jsonGenerator.toJson(nodeTags)
        }
    }

    static void hideBranchTags(Node node, String preBranchTagsJson = null) {
        if (preBranchTagsJson === null)
            preBranchTagsJson = node[preBranchTags].text
        def originalTags = jsonSlurper.parseText(preBranchTagsJson)
        if (originalTags) {
            node.tags.tags = originalTags
            node[preBranchTags] = null
        }
    }

    static void hideNodeTags(Node node) {
        def nodeTags = node.tags.tags
        if (nodeTags) {
            node.tags.tags = []
            node[preHideTags] = jsonGenerator.toJson(nodeTags)
        }
    }

    static void showNodeTags(Node node, String preTagsHiddenJson = null) {
        if (preTagsHiddenJson == null)
            preTagsHiddenJson = node[preHideTags].text
        def originalTags = jsonSlurper.parseText(preTagsHiddenJson)
        if (originalTags) {
            node.tags.tags = originalTags
            node[preHideTags] = null
        }
    }
}
