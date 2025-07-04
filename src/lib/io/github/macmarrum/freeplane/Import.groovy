/*
 * Copyright (C) 2023 - 2025  macmarrum (at) outlook (dot) ie
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
import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.util.FreeplaneVersion
import org.freeplane.core.util.Hyperlink
import org.freeplane.core.util.LogUtils
import org.freeplane.features.format.FormattedDate
import org.freeplane.features.format.FormattedFormula
import org.freeplane.features.format.FormattedNumber
import org.freeplane.features.format.FormattedObject
import org.freeplane.plugin.script.proxy.ScriptUtils

import javax.swing.JFileChooser
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.regex.Pattern

class Import {
    private static final String COLON = ':'
    private static final String COMMA = ','
    private static final String DOUBLE_QUOTE = '"'
    private static final String HASH = '#'
    private static final String NL = '\n'
    private static final String CR = '\r'
    private static final String PILCROW = '\u00B6'
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
    private static final String PROPS = '@props'
    private static final String STYLE = '@style'
    private static final String TAGS = '@tags'
    private static final String TEXT_COLOR = '@textColor'
    private static final String HYPERLINK = Hyperlink.class.simpleName
    private static final String URI_ = URI.class.simpleName
    private static final String FORMATTED_DATE = FormattedDate.class.simpleName
    private static final String FORMATTED_NUMBER = FormattedNumber.class.simpleName
    private static final String FORMATTED_FORMULA = FormattedFormula.class.simpleName
    private static final String FORMATTED_OBJECT = FormattedObject.class.simpleName
    public static Charset charset = StandardCharsets.UTF_8
    private static final Controller c = ScriptUtils.c()
    private static final FreeplaneVersion FP_VER = FreeplaneVersion.version
    private static final FreeplaneVersion FP_1_12_1 = FreeplaneVersion.getVersion('1.12.1')
    private static final FreeplaneVersion FP_1_12_12 = FreeplaneVersion.getVersion('1.12.12')
    public static csvSettings = [sep: COMMA, np: NodePart.CORE, nl: null]

    enum NodePart {
        CORE, DETAILS, NOTE
    }

    static File askForFile(File suggestedFile = null) {
        final fileChooser = new JFileChooser()
        fileChooser.multiSelectionEnabled = false
        if (suggestedFile)
            fileChooser.selectedFile = suggestedFile
        final returnVal = fileChooser.showOpenDialog()
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return
        }
        return fileChooser.getSelectedFile()
    }

    static String decodeBase64(String base64) {
        return new String(base64.decodeBase64())
    }

    static Node fromJsonFile(File file, Node parent = null, Boolean shouldFold = null) {
        return fromJsonString(file.getText(charset.name()), parent, shouldFold)
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
            def isValueStrOrNum = isValueString || value instanceof Number
            def isValueMap = !isValueStrOrNum && value instanceof Map
            def isValueList = !isValueStrOrNum && !isValueMap && value instanceof List
            switch (key) {
                case CORE -> {
                    if (isValueStrOrNum)
                        node.object = value
                    else if (isValueList)
                        _fromJson_setCoreFromList(value as List, node)
                    else
                        throw new IllegalArgumentException("${node.id}: got ${CORE} of type ${value.class.simpleName} - expected String, Number, List")
                }
                case DETAILS -> {
                    if (isValueString)
                        node.details = value
                    else if (isValueList) {
                        node.detailsContentType = value[1]
                        node.details = value[0]
                    } else
                        throw new IllegalArgumentException("${node.id}: got ${DETAILS} of type ${value.class.simpleName} - expected String, List")
                }
                case NOTE -> {
                    if (isValueString)
                        node.note = value
                    else if (isValueList) {
                        node.noteContentType = value[1]
                        node.note = value[0]
                    } else
                        throw new IllegalArgumentException("${node.id}: got ${NOTE} of type ${value.class.simpleName} - expected String, List")
                }
                case ATTRIBUTES -> {
                    if (isValueMap)
                        value.each { String aName, aValue -> node.attributes.add(aName, aValue) }
                    else if (isValueList)
                        value.each { List<Object> l -> _fromJson_setAttributeFromList(node, l) }
                    else
                        throw new IllegalArgumentException("${node.id}: got ${ATTRIBUTES} of type ${value.class.simpleName} - expected Map, List")
                }
                case LINK -> {
                    if (isValueString)
                        node.link.uri = new URI(value as String)
                    else
                        throw new IllegalArgumentException("${node.id}: got ${LINK} of type ${value.class.simpleName} - expected String")
                }
                case STYLE -> {
                    if (isValueString) {
                        // deprecated in favor of style map
                        try {
                            node.style.name = value
                        } catch (IllegalArgumentException e) {
                            LogUtils.severe("${node.id} ${STYLE}: ${e}")
                        }
                    } else if (isValueMap) {
                        def map = value as Map<String, Object>
                        def style = node.style
                        for (entry in map.entrySet()) {
                            if (entry.key == 'numbering' && FP_VER < FP_1_12_12) {
                                try {
                                    style.setNumberingEnabled(entry.value)
                                } catch (IllegalArgumentException e) {
                                    LogUtils.severe("${node.id} ${entry.key}: ${e}")
                                }
                                continue
                            }
                            // key can be a chain of props, e.g. border.colorCode
                            def current = style
                            def props = entry.key.tokenize('.')
                            for (prop in props[0..<-1])
                                current = current."$prop"
                            try {
                                current."${props[-1]}" = entry.value
                            } catch (IllegalArgumentException e) {
                                LogUtils.severe("${node.id} ${entry.key}: ${e}")
                            }
                        }
                    } else
                        throw new IllegalArgumentException("${node.id}: got ${STYLE} of type ${value.class.simpleName} - expected Map<String, Object>")
                }
                case PROPS -> {
                    if (isValueMap) {
                        def map = value as Map<String, Object>
                        for (entry in map.entrySet()) {
                            // key can be a chain of props, e.g. cloud.shape
                            def current = node
                            def props = entry.key.tokenize('.')
                            for (prop in props[0..<-1])
                                current = current."$prop"
                            try {
                                current."${props[-1]}" = entry.value
                            } catch (IllegalArgumentException e) {
                                LogUtils.severe("${node.id} ${entry.key}: ${e}")
                            }
                        }
                    } else
                        throw new IllegalArgumentException("${node.id}: got ${PROPS} of type ${value.class.simpleName} - expected Map<String, Object>")
                }
                case ICONS -> {
                    if (isValueList)
                        node.icons.addAll(value as List<String>)
                    else
                        throw new IllegalArgumentException("${node.id}: got ${STYLE} of type ${value.class.simpleName} - expected List<String>")
                }
                case TAGS -> {
                    if (FP_VER >= FP_1_12_1) {
                        if (isValueList)
                            node.tags.tags = value as List<String>
                        else
                            throw new IllegalArgumentException("${node.id}: got ${TAGS} of type ${value.class.simpleName} - expected List<String>")
                    }
                }
                case BACKGROUND_COLOR -> {
                    // deprecated in favor of @style
                    if (isValueString)
                        node.style.backgroundColorCode = value
                    else
                        throw new IllegalArgumentException("${node.id}: got ${BACKGROUND_COLOR} of type ${value.class.simpleName} - expected String")
                }
                case TEXT_COLOR -> {
                    // deprecated in favor of @style
                    if (isValueString)
                        node.style.textColorCode = value
                    else
                        throw new IllegalArgumentException("${node.id}: got ${TEXT_COLOR} of type ${value.class.simpleName} - expected String")
                }
                default -> {
                    def n = node.createChild(key)
                    if (isValueMap)
                        _fromJsonMapRecursively(value as Map, n)
                    else if (isValueList)
                        _fromJsonList(value as List, n)
                    else if (value !== null)
                        n.createChild().object = value
                }
            }
        }
    }

    static void _fromJson_setCoreFromList(List value, Node node) {
        def valueListSize = value.size()
        switch (valueListSize) {
            case 1 -> { // number, string/formula
                node.object = value[0]
            }
            case 3 -> { // number, string/formula, date with no format
                String val, type, pattern_or_format
                (val, type, pattern_or_format) = value
                if (type == FORMATTED_DATE) {
                    try {
                        def date = _parseDate(val, pattern_or_format)
                        node.object = new FormattedDate(date, pattern_or_format)
                    } catch (DateTimeParseException | IllegalArgumentException e) {
                        LogUtils.severe("${node.id} ${CORE}: ${e}")
                        node.text = val
                    }
                } else {
                    node.format = pattern_or_format
                    node.object = val
                }
            }
            case 4 -> {
                String val, type, pattern, format
                (val, type, pattern, format) = value
                if (type == FORMATTED_DATE) {
                    try {
                        node.format = format
                        def date = _parseDate(val, pattern, format)
                        node.object = new FormattedDate(date, pattern)
                    } catch (DateTimeParseException | IllegalArgumentException e) {
                        LogUtils.severe("${node.id} ${CORE}: ${e}")
                        node.text = val
                    }
                } else {
                    throw new IllegalArgumentException("${node.id} ${CORE}: in a list of ${valueListSize} elems, found type ${type} - expected ${FORMATTED_DATE}")
                }
            }
        }
    }

    /** Parses a date value produced by Export.
     * Expects value to be in the format resulting from ISO_LOCAL or ISO or DISPLAYED.
     */
    static Date _parseDate(String value, String fallbackPattern, String fallbackFormat = null) {
        def valueSize = value.size()
        // DateFmt.ISO_LOCAL date
        if (valueSize == 10 && !value.contains(COLON))
            return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE).toDate()
        // DateFmt.ISO (date/time)
        try {
            return OffsetDateTime.parse(value).toDate()
        } catch (DateTimeParseException ignore) {
        }
        // DateFmt.ISO_LOCAL date/time
        if (valueSize == 19 && value.contains(COLON)) {
            try {
                return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME).toDate()
            } catch (DateTimeParseException ignore) {
            }
        }
        // format - could be `Date: %s`
        if (fallbackFormat) {
            try {
                // `Date: %s` is not a date/time pattern so it will fail
                def dtf = DateTimeFormatter.ofPattern(fallbackFormat)
                try {
                    return OffsetDateTime.parse(value, dtf).toDate()
                } catch (DateTimeParseException ignore) {
                }
                try {
                    return LocalDateTime.parse(value, dtf).toDate()
                } catch (DateTimeParseException ignore) {
                }
                try {
                    return LocalDate.parse(value, dtf).toDate()
                } catch (DateTimeParseException ignore) {
                }
            } catch (IllegalArgumentException ignore) {
            }
        }
        // pattern
        def dtf = DateTimeFormatter.ofPattern(fallbackPattern)
        try {
            return OffsetDateTime.parse(value, dtf).toDate()
        } catch (DateTimeParseException ignore) {
        }
        try {
            return LocalDateTime.parse(value, dtf).toDate()
        } catch (DateTimeParseException ignore) {
        }
        return LocalDate.parse(value, dtf).toDate()
    }

    static simpleNameToClass = new HashMap<String, Class>()
    static {
        simpleNameToClass.put(HYPERLINK, Hyperlink)
        simpleNameToClass.put(URI_, URI)
        simpleNameToClass.put(FORMATTED_DATE, FormattedDate)
        simpleNameToClass.put(FORMATTED_NUMBER, FormattedNumber)
        simpleNameToClass.put(FORMATTED_FORMULA, FormattedFormula)
        simpleNameToClass.put(FORMATTED_OBJECT, FormattedObject)
    }

    static void _fromJson_setAttributeFromList(Node node, List<Object> l) {
        def listSize = l.size()
        String name = l[0]
        def value = l[1]
        switch (listSize) {
            case 2 -> {
                node.attributes.add(name, value)
            }
            case 3 -> {
                String simpleName = l[2]
                assert simpleName in [HYPERLINK, URI_]
                def uri = new URI(value as String)
                def obj = simpleNameToClass[simpleName].newInstance(uri)
                node.attributes.add(name, obj)
            }
            case 4 -> {
                String simpleName = l[2]
                String pattern = l[3]
                assert simpleName in [FORMATTED_DATE, FORMATTED_NUMBER, FORMATTED_FORMULA, FORMATTED_OBJECT]
                def obj = null
                if (simpleName == FORMATTED_DATE) {
                    try {
                        def date = _parseDate(value as String, pattern)
                        obj = new FormattedDate(date, pattern)
                    } catch (DateTimeParseException | IllegalArgumentException e) {
                        LogUtils.severe("${node.id} ${ATTRIBUTES} ${name}: ${e}")
                        obj = value as String
                    }
                } else {
                    obj = simpleNameToClass[simpleName].newInstance(value, pattern)
                }
                node.attributes.add(name, obj)
            }
            default -> throw new IllegalArgumentException("${node.id} ${ATTRIBUTES}: ${listSize} elements in list ${l} - expected 2 or 3 or 4")
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
        def reader = new StringReader(content)
        _fromCsvReader(reader, node, settings)
    }

    static void fromCsvFile(File file, Node node, HashMap<String, Object> settings = null) {
        def reader = new InputStreamReader(file.newInputStream(), charset)
        _fromCsvReader(reader, node, settings)
        reader.close()
    }

//    static void fromCsvInputStream(InputStream inputStream, Node node, HashMap<String, Object> settings) {
//        def reader = new InputStreamReader(inputStream, charset)
//        _fromCsvReader(reader, node, settings)
//        reader.close()
//    }

    static void _fromCsvReader(Reader reader, Node node, HashMap<String, Object> settings) {
        settings = !settings ? csvSettings.clone() : csvSettings + settings
        try {
            def csvFormat = (Class.forName('org.apache.commons.csv.CSVFormat').EXCEL).builder().setDelimiter(settings.sep as String).build()
            csvFormat.parse(reader).each { csvRecord -> _fromCsvRow(csvRecord.values() as Iterable<String>, node, settings) }
        } catch (ClassNotFoundException ignored) {
            def msg = "(!) 'Apache Commons CSV' (optional) not found in classpath - download it to `<user-dir>/lib`: https://repo1.maven.org/maven2/org/apache/commons/commons-csv/1.10.0/commons-csv-1.10.0.jar"
            c.statusInfo = msg
            println(msg)
            reader.eachLine { line -> if (line) _fromCsvLine(line, node, settings) }
        }
    }

    static void _fromCsvLine(String line, Node node, HashMap<String, Object> settings) {
        def sep = settings.sep as String
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
        _fromCsvRow(row, node, settings)
    }

    static void _fromCsvRow(Iterable<String> row, Node node, HashMap<String, Object> settings) {
        def nodePart = settings.getOrDefault('nodePart', settings.np) as NodePart
        def newlineReplacement = settings.getOrDefault('newlineReplacement', settings.nl) as String
        row.each { text ->
            if (newlineReplacement != null)
                text = text.replaceAll(newlineReplacement, NL)
            node = node.createChild()
            switch (nodePart) {
                case NodePart.CORE -> node.text = text
                case NodePart.DETAILS -> node.details = text
                case NodePart.NOTE -> node.note = text
                default -> node.text = '#ERR!'
            }
        }
    }

    static void fromHtmlTableClipboard(Node node, HashMap<String, Object> settings = null) {
        def transferable = Toolkit.defaultToolkit.systemClipboard.getContents(null)
        if (transferable.isDataFlavorSupported(DataFlavor.allHtmlFlavor)) {
            try {
                def Jsoup = Class.forName('org.jsoup.Jsoup')
                def html = transferable.getTransferData(DataFlavor.allHtmlFlavor)
                def doc = Jsoup.parse(html)
                def lol = doc.select('table').first().select('tr').collect { row -> row.select('th, td').collect { it.wholeText() } }
                Node child
                for (Collection<String> tableRow in lol) {
                    child = node
                    for (String cellText in tableRow) {
                        child = child.createChild(cellText)
                    }
                }
            } catch (ClassNotFoundException ignored) {
                def msg = '(!) Jsoup not found in classpath - add `<Freeplane-install-dir>/plugins/org.freeplane.plugin.markdown/lib` to `Preferences…->Plugins->Scripting->Script classpath: Additional directories containing classes and/or JARs (see tooltip)`'
                c.statusInfo = msg
                println(msg)
            }
        } else {
            c.statusInfo = '(!) no HTML in clipboard'
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
                        throw new IllegalArgumentException("Heading ${level} was requested to be imported but no heading ${level - 1} was found to attach it to: ${line}")
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
