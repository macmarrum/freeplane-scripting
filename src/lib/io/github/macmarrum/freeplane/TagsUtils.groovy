// Copyright (C) 2024  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.macmarrum.freeplane

import groovy.json.JsonGenerator
import groovy.json.JsonSlurper
import org.freeplane.api.Node

class TagsUtils {
    public static final String preBranchTags = 'preBranchTags'
    private static final JsonGenerator jsonGenerator = new JsonGenerator.Options().disableUnicodeEscaping().build()
    private static final JsonSlurper jsonSlurper = new JsonSlurper()

    static void showBranchTags(Node node) {
        def branchTags = new TreeSet<String>()
        node.findAll().each {
            branchTags.addAll(it.tags.tags)
        }
        def nodeTags = node.tags.tags
        if (branchTags != new TreeSet(nodeTags)) {
            node[preBranchTags] = jsonGenerator.toJson(nodeTags)
            node.tags.tags = branchTags
        }
    }

    static void hideBranchTags(Node node, String preBranchTagsJson = null) {
        if (preBranchTagsJson === null)
            preBranchTagsJson = node[preBranchTags].text
        def originalTags = jsonSlurper.parseText(preBranchTagsJson)
        node.tags.tags = originalTags
        node[preBranchTags] = null
    }
}
