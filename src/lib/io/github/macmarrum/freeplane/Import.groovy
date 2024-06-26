/*
 * Copyright (C) 2023, 2024  macmarrum (at) outlook (dot) ie
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

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

class Import {
    private static final String COMMA = ','
    private static final String DOUBLE_QUOTE = '"'
    private static final String HASH = '#'
    private static final String NL = '\n'
    private static final String SPACE = ' '
    private static final String TWO_DOUBLE_QUOTES = '""'
    private static final Pattern RX_TWO_DOUBLE_QUOTES = ~/""/
    private static final String ATTRIBUTES = '@attributes'
    private static final String BACKGROUND_COLOR = '@backgroundColor'
    private static final String CORE = '@core'
    private static final String DETAILS = '@details'
    private static final String ICONS = '@icons'
    private static final String LINK = '@link'
    private static final String NOTE = '@note'
    private static final String STYLE = '@style'
    private static final String TEXT_COLOR = '@textColor'
    public static Charset charset = StandardCharsets.UTF_8
    public static csvSettings = [sep: COMMA, np: NodePart.CORE]

    enum NodePart {
        CORE, DETAILS, NOTE
    }

    static String decodeBase64(String base64) {
        return new String(base64.decodeBase64())
    }

    static Node fromJsonFile(File file, Node parent = null, Boolean shouldFold = null) {
        fromJsonString(file.getText(charset.name()), parent, shouldFold)
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
            def isValueString = value instanceof String
            def isValueMap = !isValueString && value instanceof Map
            def isValueList = !isValueString && !isValueMap && value instanceof List
            if (key == CORE && isValueString) {
                node.text = value
            } else if (key == DETAILS && isValueString) {
                node.details = value
            } else if (key == NOTE && isValueString) {
                node.note = value
            } else if (key == ATTRIBUTES && isValueMap) {
                value.each { attrName, attrValue -> node[attrName as String] = attrValue }
            } else if (key == LINK && isValueString) {
                node.link.uri = new URI(value as String)
            } else if (key == STYLE && isValueString) {
                node.style.name = value
            } else if (key == ICONS && isValueList) {
                node.icons.addAll(value)
            } else if (key == BACKGROUND_COLOR && isValueString) {
                node.style.backgroundColorCode = value
            } else if (key == TEXT_COLOR && isValueString) {
                node.style.textColorCode = value
            } else if (isValueMap) {
                def n = node.createChild(key)
                _fromJsonMapRecursively(value as Map, n)
            } else if (isValueList) {
                def n = node.createChild(key)
                _fromJsonList(value, n)
            } else {
                def n = node.createChild(key)
                if (value !== null) {
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
        def inputStream = new ByteArrayInputStream(content.getBytes(charset))
        fromCsvInputStream(inputStream, node, settings)
    }

    static void fromCsvFile(File file, Node node, HashMap<String, Object> settings = null) {
        fromCsvInputStream(file.newInputStream(), node, settings)
    }

    static void fromCsvInputStream(InputStream inputStream, Node node, HashMap<String, Object> settings) {
        settings = !settings ? csvSettings.clone() : csvSettings + settings
        def sep = settings.sep as String
        def nodePart = settings.getOrDefault('nodePart', settings.np) as NodePart
        inputStream.eachLine(charset.name()) { line -> if (line) _fromCsvLine(line, node, sep, nodePart) }
    }

    static void _fromCsvLine(String line, Node node, String sep = COMMA, NodePart nodePart = NodePart.CORE) {
        assert sep != DOUBLE_QUOTE
        def row = new LinkedList<String>()
        def cell = new StringBuilder()
        def isQtOpen = false
        def endIdx = line.size() - 1
        line.eachWithIndex { c, i ->
            if (isQtOpen) {
                // ", => end of quoted text
                // """, => an escaped quote then end of quoted text
                if (c == DOUBLE_QUOTE // a quote
                        // preceded by a non-quote or by a double quote
                        && (i == 0 || line[i - 1] != DOUBLE_QUOTE || (i > 1 && line[i - 2..i - 1] == TWO_DOUBLE_QUOTES))
                        // and followed by a sep
                        && (i == endIdx || line[i + 1] == sep)
                )
                    isQtOpen = false
                else
                    cell << c
            } else {
                // ," => start of quoted text
                // ,""" => start of quoted text then an escaped quote
                if (c == DOUBLE_QUOTE // a quote
                        // preceded by a sep
                        && (i == 0 || line[i - 1] == sep)
                        // and followed by a non-quote or a double quote
                        && (i == endIdx || line[i + 1] != DOUBLE_QUOTE || (i < endIdx - 2 && line[i + 1..i + 2] == TWO_DOUBLE_QUOTES))
                )
                    isQtOpen = true
                else if (c == sep) {
                    row << cell.replaceAll(RX_TWO_DOUBLE_QUOTES, DOUBLE_QUOTE)
                    cell.length = 0
                } else
                    cell << c
            }
        }
        if (isQtOpen)
            throw new IllegalArgumentException("unterminated quote in: ${line}")
        row << cell.replaceAll(RX_TWO_DOUBLE_QUOTES, DOUBLE_QUOTE)
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

    static void fromMarkdownString(String markdown, Node node) {
        def topNodeLevel = node.getNodeLevel(true)
        def n = node
        Node parent
        String hashes
        int level
        String text
        markdown.split(NL).each { line ->
            if (line.startsWith(HASH)) {
                // a heading
                (hashes, text) = line.split(SPACE, 2)
                level = hashes.stripTrailing().size()
                text = text.stripLeading()
                // find a "level - 1" node, i.e. a parent for the new node
                if (level == 1)
                    parent = node
                else {
                    // find the last node of (level - 1)
                    List<Node> lst = node.find { it.getNodeLevel(true) == (topNodeLevel + level - 1) }
                    if (lst)
                        parent = lst.last()
                    else
                        throw new RuntimeException("Heading ${level} was requested to be imported but no heading ${level - 1} was found to attach it to: ${line}")
                }
                n = parent.createChild(text)
            } else {
                // not a heading -- import into details of the last heading
                def detailsText = n.details?.text
                if (detailsText || line) { // skip empty lines at the beginning
                    n.details = detailsText ? detailsText + NL + line : line
                }
            }
        }
    }
}
