/*
 * Copyright (C) 2023  macmarrum (at) outlook (dot) ie
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.macmarrum.freeplane

import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import org.freeplane.api.Node
import org.freeplane.plugin.script.proxy.ScriptUtils

import java.nio.charset.StandardCharsets

import static io.github.macmarrum.freeplane.Export.COMMA
import static io.github.macmarrum.freeplane.Export.NodePart

class Import {
    private static final DETAILS = '@details'
    private static final ATTRIBUTES = '@attributes'
    private static final NOTE = '@note'
    private static final STYLE = '@style'
    private static final ICONS = '@icons'
    private static final UTF8 = StandardCharsets.UTF_8.name()
    private static final QT = '"'
    private static final DOUBLE_QT = '""'
    private static final RX_DOUBLE_QT = ~/""/

    static String decodeBase64(String base64) {
        return new String(base64.decodeBase64())
    }

    static Node fromJsonFile(File file, Node parent = null, Boolean shouldFold = null) {
        fromJsonString(file.getText(UTF8), parent, shouldFold)
    }

    static Node fromJsonStringBase64(String base64, Node parent = null, Boolean shouldFold = null) {
        return fromJsonString(decodeBase64(base64), parent, shouldFold)
    }

    static Node fromJsonString(String content, Node parent = null, Boolean shouldFold = null) {
        if (shouldFold == null)
            shouldFold = false
        if (!parent)
            parent = ScriptUtils.node().mindMap.root.createChild('JSON')
        fold(shouldFold, parent)
        def jObject = new JsonSlurper(type: JsonParserType.CHAR_BUFFER).parseText(content)
        if (jObject instanceof Map)
            _fromJsonMapRecursively(jObject, parent)
        else if (jObject instanceof List)
            _fromJsonList(jObject, parent)
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

    static void _fromJsonMapRecursively(Map<String, Object> jMap, Node node) {
        jMap.each { key, value ->
            if (key == DETAILS && value !instanceof Map && value !instanceof List) {
                node.details = value
            } else if (key == NOTE && value !instanceof Map && value !instanceof List) {
                node.note = value
            } else if (key == ATTRIBUTES && value instanceof Map) {
                value.each { attrName, attrValue -> node[attrName as String] = attrValue }
            } else if (key == STYLE && value instanceof String) {
                node.style.name = value
            } else if (key == ICONS && value instanceof List) {
                node.icons.addAll(value)
            } else if (value instanceof Map) {
                def n = node.createChild(key)
                _fromJsonMapRecursively(value as Map, n)
            } else if (value instanceof List) {
                def n = node.createChild(key)
                _fromJsonList(value, n)
            } else {
                def n = node.createChild(key)
                if (value instanceof Map) {
                    _fromJsonMapRecursively(value as Map, n)
                } else if (value instanceof List) {
                    _fromJsonList(value, n)
                } else if (value !== null) {
                    def child = n.createChild()
                    // value can be a number, and createChild() does something strange with a number
                    // so use setText() instead
                    child.text = value
                }
            }
        }
    }

    static void _fromJsonList(List list, Node node) {
        list.each {
            if (it instanceof Map) {
                _fromJsonMapRecursively(it, node)
            } else {
                def child = node.createChild()
                child.text = it
            }
        }
    }

    static void fromCsvString(String content, Node node, HashMap<String, Object> settings = null) {
        def inputStream = new ByteArrayInputStream(content.getBytes(UTF8))
        fromCsvInputStream(inputStream, node, settings)
    }

    static void fromCsvFile(File file, Node node, HashMap<String, Object> settings = null) {
        fromCsvInputStream(file.newInputStream(), node, settings)
    }

    static void fromCsvInputStream(InputStream inputStream, Node node, HashMap<String, Object> settings) {
        settings = !settings ? Export.csvSettings.clone() : Export.csvSettings + settings
        def sep = settings.sep as String
        def with1 = settings.with1 as boolean
        def nodePart = settings.getOrDefault('nodePart', settings.np) as NodePart
        inputStream.eachLine(UTF8) { line -> if (line) _fromCsvLine(line, node, sep, nodePart) }
    }

    static void _fromCsvLine(String line, Node node, String sep = COMMA, NodePart nodePart = NodePart.CORE) {
        assert sep != QT
        def row = new LinkedList<String>()
        def cell = new StringBuilder()
        def isQtOpen = false
        def endIdx = line.size() - 1
        line.eachWithIndex { c, i ->
            if (isQtOpen) {
                // ", => end of quoted text
                // """, => an escaped quote then end of quoted text
                if (c == QT // a quote
                        // preceded by a non-quote or by a double quote
                        && (i == 0 || line[i - 1] != QT || (i > 1 && line[i - 2..i - 1] == DOUBLE_QT))
                        // and followed by a sep
                        && (i == endIdx || line[i + 1] == sep)
                )
                    isQtOpen = false
                else
                    cell << c
            } else {
                // ," => start of quoted text
                // ,""" => start of quoted text then an escaped quote
                if (c == QT // a quote
                        // preceded by a sep
                        && (i == 0 || line[i - 1] == sep)
                        // and followed by a non-quote or a double quote
                        && (i == endIdx || line[i + 1] != QT || (i < endIdx - 2 && line[i + 1..i + 2] == DOUBLE_QT))
                )
                    isQtOpen = true
                else if (c == sep) {
                    row << cell.replaceAll(RX_DOUBLE_QT, QT)
                    cell.length = 0
                } else
                    cell << c
            }
        }
        if (isQtOpen)
            throw new IllegalArgumentException("unterminated quote in: ${line}")
        row << cell.replaceAll(RX_DOUBLE_QT, QT)
        _fromCsvRow(row, node, nodePart)
    }

    static void _fromCsvRow(List<String> row, Node node, NodePart nodePart = NodePart.CORE) {
        row.each { text ->
            node = node.createChild()
            switch (nodePart) {
                case NodePart.CORE -> node.text = text
                case NodePart.DETAILS -> node.details = text
                case NodePart.NOTE -> node.note = text
                default -> node.text = '#ERR!'
            }
        }
    }
}
