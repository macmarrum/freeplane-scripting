package io.github.macmarrum.freeplane

import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import org.freeplane.api.Node
import org.freeplane.plugin.script.proxy.ScriptUtils

class Import {
    private static String DETAILS = '@details'
    private static String ATTRIBUTES = '@attributes'
    private static String NOTE = '@note'

    static String decodeBase64(String base64) {
        return new String(base64.decodeBase64())
    }

    static Node importJsonBase64(String base64, Node parent = null, boolean shouldFold = false) {
        return importJson(decodeBase64(base64), parent, shouldFold)
    }

    static Node importJson(String content, Node parent = null, boolean shouldFold = false) {
        if (!parent)
            parent = ScriptUtils.node().mindMap.root.createChild('JSON')
        fold(shouldFold, parent)
        def jObject = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parseText(content)
        if (jObject instanceof Map)
            importMapRecursively(jObject, parent)
        else if (jObject instanceof List)
            importList(jObject, parent)
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

    static void importMapRecursively(Map<String, Object> jMap, Node node) {
        jMap.each { key, value ->
            if (key == DETAILS && value !instanceof Map && value !instanceof List) {
                node.details = value
            } else if (key == ATTRIBUTES && value instanceof Map) {
                value.each { attrName, attrValue -> node[attrName as String] = attrValue }
            } else if (key == NOTE && value !instanceof Map && value !instanceof List) {
                node.note = value
            }else if (value instanceof Map) {
                def n = node.createChild(key)
                importMapRecursively(value as Map, n)
            } else if (value instanceof List) {
                def n = node.createChild(key)
                importList(value, n)
            } else {
                def n = node.createChild(key)
                if (value instanceof Map) {
                    importMapRecursively(value as Map, n)
                } else if (value instanceof List) {
                    importList(value, n)
                } else if (value !== null) {
                    def child = n.createChild()
                    child.text = value
                }
            }
        }
    }

    static void importList(List list, Node node) {
            list.each {
                if (it instanceof Map) {
                    importMapRecursively(it, node)
                } else {
                    def child = node.createChild()
                    child.text = it
                }
            }
    }
}
