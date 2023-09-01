package io.github.macmarrum.freeplane
/*
Copyright (C) 2023  macmarrum

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import org.freeplane.api.Node
import org.freeplane.plugin.script.proxy.ScriptUtils

import java.nio.charset.StandardCharsets

class Import {
    private static String DETAILS = '@details'
    private static String ATTRIBUTES = '@attributes'
    private static String NOTE = '@note'
    private static String UTF8 = StandardCharsets.UTF_8.name()

    static String decodeBase64(String base64) {
        return new String(base64.decodeBase64())
    }

    static Node fromJsonFile(File file, Node parent = null, boolean shouldFold = false) {
        fromJsonString(file.getText(UTF8), parent, shouldFold)
    }

    static Node fromJsonStringBase64(String base64, Node parent = null, boolean shouldFold = false) {
        return fromJsonString(decodeBase64(base64), parent, shouldFold)
    }

    static Node fromJsonString(String content, Node parent = null, boolean shouldFold = false) {
        if (!parent)
            parent = ScriptUtils.node().mindMap.root.createChild('JSON')
        fold(shouldFold, parent)
        def jObject = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parseText(content)
        if (jObject instanceof Map)
            fromMapRecursively(jObject, parent)
        else if (jObject instanceof List)
            fromList(jObject, parent)
        unfold(shouldFold, parent)
        return parent
    }

    private static void fold(boolean shouldFold, Node parent) {
        if (shouldFold && !parent.root) {
            def child = parent.createChild('delete me')
            parent.folded = true
            child.delete()
        }
    }

    private static void unfold(boolean shouldFold, Node parent) {
        if (shouldFold && !parent.root) {
            parent.folded = false
        }
    }

    static void fromMapRecursively(Map<String, Object> jMap, Node node) {
        jMap.each { key, value ->
            if (key == DETAILS && value !instanceof Map && value !instanceof List) {
                node.details = value
            } else if (key == ATTRIBUTES && value instanceof Map) {
                value.each { attrName, attrValue -> node[attrName as String] = attrValue }
            } else if (key == NOTE && value !instanceof Map && value !instanceof List) {
                node.note = value
            } else if (value instanceof Map) {
                def n = node.createChild(key)
                fromMapRecursively(value as Map, n)
            } else if (value instanceof List) {
                def n = node.createChild(key)
                fromList(value, n)
            } else {
                def n = node.createChild(key)
                if (value instanceof Map) {
                    fromMapRecursively(value as Map, n)
                } else if (value instanceof List) {
                    fromList(value, n)
                } else if (value !== null) {
                    def child = n.createChild()
                    child.text = value
                }
            }
        }
    }

    static void fromList(List list, Node node) {
        list.each {
            if (it instanceof Map) {
                fromMapRecursively(it, node)
            } else {
                def child = node.createChild()
                child.text = it
            }
        }
    }
}
