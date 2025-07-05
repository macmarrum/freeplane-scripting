/*
 * Copyright (C) 2025  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Import"})
// https://github.com/freeplane/freeplane/discussions/2559#discussioncomment-13665232

import groovy.json.JsonSlurper
import io.github.macmarrum.freeplane.Import
import org.freeplane.api.Node

import java.util.regex.Pattern

node = node as Node

static def parseTags(String tagString, Pattern rxSplitTags) {
    def tags = []
    def matcher = rxSplitTags.matcher(tagString)
    while (matcher.find()) {
        if (matcher.group(1)) {  // tag inside [[...]]
            tags.add(matcher.group(1))
        } else if (matcher.group(2)) {  // single-word tag
            tags.add(matcher.group(2))
        }
    }
    return tags
}

static def convertTiddlersToFreeplane(File inputFile) {
    def ignoredForAttributes = [
            'created',
            'modified',
            'title',
            'tags',
            'text'
    ]
    def jsonSlurper = new JsonSlurper()
    def tiddlers = jsonSlurper.parse(inputFile) as List<HashMap<String, String>>

    def freeplaneHashMap = [
            '0': [
                    '@core': inputFile.name
            ]
    ]

    // This regex captures [[tags with spaces]] and individual tags
    def rxSplitTags = ~/\[\[(.*?)]]|(\S+)/

    int idx = 0
    for (item in tiddlers) {
        def key = ++idx as String
        def hmParent = freeplaneHashMap['0']
        hmParent[key] = [
                '@core': item.title ?: "",
                '@note': item.text ?: "",
        ]
        if (item?.tags) {
            hmParent[key]['@tags'] = parseTags(item.tags ?: "", rxSplitTags)
        }
        def props = [:]
        if (item?.title?.startsWith('=')) {
            props['format'] = 'NO_FORMAT'
        }
        if (item?.type == 'text/markdown') {
            ignoredForAttributes << 'type'
            props['noteContentType'] = 'markdown'
        }
        def attributes = [:]
        for (entry in item.entrySet()) {
            if (entry.key !in ignoredForAttributes) {
                attributes[entry.key] = entry.value
            }
        }
        if (attributes) {
            hmParent[key]['@attributes'] = attributes
        }
        if (props) {
            hmParent[key]['@props'] = props
        }
    }

    // dump for debugging purposes
    //new File(inputFile.parent, 'freeplane.json').write(JsonOutput.prettyPrint(JsonOutput.toJson(freeplaneHashMap)), 'UTF-8')

    return freeplaneHashMap
}

def suggestedFile = new File(node.mindMap.file.parent, 'tiddlers.json')
def inputFile = Import.askForFile(suggestedFile)

if (inputFile) {
    def freeplaneHashMap = convertTiddlersToFreeplane(inputFile)
    Import._fromJsonMapRecursively(freeplaneHashMap, node)
}
