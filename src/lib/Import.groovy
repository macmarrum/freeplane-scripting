package io.github.macmarrum.freeplane

import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import org.freeplane.api.Node as FN
import org.freeplane.plugin.script.proxy.ScriptUtils

class Import {

    static String decodeBase64(String base64) {
        return new String(base64.decodeBase64())
    }

    static void importJsonBase64(String base64, FN parent = null) {
        importJson(decodeBase64(base64), parent)
    }

    static void importJson(String content, FN parent = null) {
        if (!parent)
            parent = ScriptUtils.node().mindMap.root.createChild('JSON')
        def jMap = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parseText(content) as Map
        importMapRecursively(jMap, parent)
    }

    static void importMapRecursively(Map<String, Object> jMap, FN node) {
        jMap.each { key, value ->
            if (value instanceof Map) {
                def n = node.createChild(key)
                importMapRecursively(value as Map, n)
            } else if (value instanceof List) {
                def n = node.createChild(key)
                value.eachWithIndex { elem, i ->
                    if (elem instanceof Map) {
                        importMapRecursively(elem as Map, n)
                    } else {
                        n.createChild(elem)
                    }
                }
            } else {
                node[key] = value
            }
        }
    }
}
