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

import groovy.json.JsonGenerator
import groovy.json.JsonOutput
import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.freeplane.core.util.HtmlUtils
import org.freeplane.core.util.Hyperlink
import org.freeplane.core.util.TextUtils
import org.freeplane.features.format.FormattedDate
import org.freeplane.features.format.FormattedFormula
import org.freeplane.features.format.FormattedNumber
import org.freeplane.features.format.FormattedObject
import org.freeplane.plugin.script.FreeplaneScriptBaseClass
import org.freeplane.plugin.script.proxy.ConvertibleNumber
import org.freeplane.plugin.script.proxy.ConvertibleText

import javax.swing.*
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.regex.Pattern

import static org.freeplane.core.util.ColorUtils.colorToRGBAString

class Export {
    private static final String COMMA = ','
    private static final String PIPE = '|'
    private static final String TAB = '\t'
    private static final String NL = '\n'
    private static final String CR = '\r'
    private static final String BLANK = ''
    private static final String SPACE = ' '
    private static final String TWO_SPACES = '  '
    private static final String FOUR_SPACES = '    '
    private static final String GT_SPACE = '> '
    private static final String HASH = '#'
    private static final Pattern RX_MULTILINE_BEGINING = ~/(?m)^/
    private static final Pattern RX_HARD_LINE_BREAK_CANDIDATE = ~/(?<!^|\\|\n)\n(?!\n|$)/
    private static final String BACKSLASH_NL = '\\\\\n'
    private static final Pattern RX_AUTOMATIC_LAYOUT_LEVEL = ~/^AutomaticLayout.level([,.])/
    private static final String ROOT = 'root'
    private static final String ZERO = '0'
    private static final Integer MIN_LEVEL_CEILING = 999
    private static final String SINGLE_QUOTE = '\''
    private static final String DOUBLE_QUOTE = '"'
    private static final String MULTILINE_SINGLE_QUOTE = '\'\'\''
    private static final String MULTILINE_DOUBLE_QUOTE = '"""'
    private static final Pattern RX_TOML_KEY = ~/[A-Za-z0-9_-]+/
    private static final Pattern RX_MD_LIST_ITEM = ~/\s*(-|\*|\d+\.)\s+\S.*/
    private static final Pattern RX_FORMULA = ~/^=(?!=).+/
    private static final String MARKDOWN_FORMAT = 'markdownPatternFormat'
    private static final RUMAR_TOML_INTEGER_SETTINGS = ['version', 'tar_format', 'compression_level', 'min_age_in_days_of_backups_to_sweep', 'number_of_backups_per_day_to_keep', 'number_of_backups_per_week_to_keep', 'number_of_backups_per_month_to_keep']
    private static final RUMAR_TOML_BOOLEAN_SETTINGS = ['checksum_comparison_if_same_size', 'file_deduplication']
    private static final RUMAR_TOML_STRING_SETTINGS = ['backup_base_dir', 'source_dir', 'backup_base_dir_for_profile', 'archive_format', 'password', 'no_compression_suffixes_default', 'no_compression_suffixes']
    private static final RUMAR_TOML_ARRAY_SETTINGS = ['included_top_dirs', 'excluded_top_dirs', 'included_dirs_as_regex', 'excluded_dirs_as_regex', 'included_files_as_glob', 'excluded_files_as_glob', 'included_files_as_regex', 'excluded_files_as_regex', 'commands_using_filters']
    private static final String ATTRIBUTES = '@attributes'
    private static final String BACKGROUND_COLOR = '@backgroundColor'
    private static final String CORE = '@core'
    private static final String DETAILS = '@details'
    private static final String ICONS = '@icons'
    private static final String LINK = '@link'
    private static final String NOTE = '@note'
    private static final String STYLE = '@style'
    private static final String TEXT_COLOR = '@textColor'
    private static final String STANDARD_FORMAT = 'STANDARD_FORMAT'
    private static final String AUTO_FORMAT = 'AUTO_FORMAT'
    public static Charset charset = StandardCharsets.UTF_8
    private static final TextUtils textUtils = new TextUtils()
    private static final FreeplaneScriptBaseClass fsbc = new FreeplaneScriptBaseClass() {
        @Override
        Object run() {
            return null
        }
    }
    public static final LEVEL_STYLE_TO_HEADING = [
            'AutomaticLayout.level.root': '#',
            'AutomaticLayout.level,1'   : '##',
            'AutomaticLayout.level,2'   : '###',
            'AutomaticLayout.level,3'   : '####',
            'AutomaticLayout.level,4'   : '#####',
            'AutomaticLayout.level,5'   : '######',
            'AutomaticLayout.level,6'   : '#######',
    ]
    public static mdSettings = [h1: MdH1.ROOT, details: MdInclude.HLB, note: MdInclude.PLAIN, lsToH: LEVEL_STYLE_TO_HEADING, skip1: false, ulStyle: 'ulBullet', olStyle: 'olBullet']
    public static csvSettings = [sep: COMMA, eol: NL, nl: CR, np: NodePart.CORE, skip1: false, tail: false, quote: false]
    public static jsonSettings = [details: true, note: true, attributes: true, style: true, formatting: true, icons: true, link: true, skip1: false, denullify: false, pretty: false, transformed: true, dateFmt: DateFmt.ISO_LOCAL, format: false, forceId: false]

    enum NodePart {
        CORE, DETAILS, NOTE
    }

    enum DateFmt {
        DISPLAYED, ISO, ISO_LOCAL
    }

    /**
     * <p>Markdown Inclusion</p>
     * <p>Reflects how input should be treated in Markdown</p>
     * <ul>
     *     <li>NONE - not included</li>
     *     <li>QUOTE - as a quote; lines will be merged</li>
     *     <li>HLB - hard line breaks for each line</li>
     *     <li>QUOTE_HLB - as a quote with a hard line break for each line</li>
     *     <li>CODE - as a code block</li>
     *     <li>PLAIN - plain text, without any modification</li>
     * </ul>
     */
    enum MdInclude {
        NONE, QUOTE, HLB, QUOTE_HLB, CODE, PLAIN
    }

    /**
     * <p>Markdown Heading1</p>
     * <p>Reflects where headings are counted from when Level Styles are encountered</p>
     * <ul>
     * <li>ROOT - root: #, Level 1: ##, etc.</li>
     * <li>NODE - the first encountered Level Style: #, the second: ##, etc</li>
     * </ul>
     */
    enum MdH1 {
        NONE, ROOT, NODE
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
        def file = fileChooser.getSelectedFile()
        if (file.exists()) {
            def message = "The file exists\n${file.path}.\nOverwrite?"
            def title = 'Confirm overwrite'
            def decision = UITools.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
            if (decision != JOptionPane.YES_OPTION)
                return
        }
        return file
    }

    static void toMarkdownFile(File file, Node node, HashMap<String, Object> settings = null) {
        def outputStream = new BufferedOutputStream(new FileOutputStream(file))
        toMarkdownOutputStream(outputStream, node, settings)
        outputStream.close()
    }

    /**
     * https://github.com/freeplane/freeplane/issues/333
     */
    static String toMarkdownString(Node node, HashMap<String, Object> settings = null) {
        def outputStream = new ByteArrayOutputStream()
        toMarkdownOutputStream(outputStream, node, settings)
        outputStream.toString(charset)
    }

    /**
     * Output to Markdown
     *
     * @param outputStream the stream to write to
     * @param node the starting node for the export (see also settings.skip1)
     * @param settings a hashMap -- see mdSettings for default values =>
     *  h1 -- where the heading level is counted from when Level Styles are encountered;
     *  details -- how to treat details in Markdown output;
     *  note -- how to treat note in Markdown output;
     *  lsToH -- a mapping of Level-Styles to hashes for headings;
     *  skip1 -- whether to skip the first node;
     *  ulStyle -- the style of nodes to be output as ul bullets;
     *  olStyle -- the style of nodes to be output as ol bullets;
     */
    static String toMarkdownOutputStream(OutputStream outputStream, Node node, HashMap<String, Object> settings = null) {
        settings = !settings ? mdSettings.clone() : mdSettings + settings
        def bNL = NL.getBytes(charset)
        def bSPACE = SPACE.getBytes(charset)
        def mdH1 = settings.h1 as MdH1
        def ulOlStyles = [settings.ulStyle as String, settings.olStyle as String]
        def levelStyleToHeading = settings.getOrDefault('levelStyleToHeading', settings.lsToH) as Map<String, String>
        def bLevelStyleToHeading
        if (mdH1 == MdH1.ROOT) {
            bLevelStyleToHeading = levelStyleToHeading.collectEntries { k, v -> [k, v.getBytes(charset)] }
        }
        def nodeToStyles = new LinkedHashMap<Node, List<String>>()
        node.find { it.visible }.eachWithIndex { it, i ->
            if (!(i == 0 && settings.skip1))
                nodeToStyles[it] = it.style.allActiveStyles
        }
        Integer minStyleLevelNum = MIN_LEVEL_CEILING
        def nodeToStyleLevelNum = new HashMap<Node, Integer>()
        if (mdH1 == MdH1.NODE) {
            nodeToStyles.each { n, allActiveStyles ->
                def assignedLevelStyle = allActiveStyles.find { it in levelStyleToHeading }
                if (assignedLevelStyle) {
                    def styleLevelStr = assignedLevelStyle.replaceAll(RX_AUTOMATIC_LAYOUT_LEVEL, BLANK).replace(ROOT, ZERO)
                    def styleLevelNum = styleLevelStr as Integer
                    nodeToStyleLevelNum[n] = styleLevelNum
                    if (styleLevelNum < minStyleLevelNum)
                        minStyleLevelNum = styleLevelNum
                }
            }
        }
        nodeToStyles.eachWithIndex { n, allActiveStyles, i ->
            if (i > 0) outputStream.write(bNL)
            boolean isHeading = false
            switch (mdH1) {
                case MdH1.ROOT -> {
                    for (def styleName in allActiveStyles) {
                        if (bLevelStyleToHeading.containsKey(styleName)) {
                            isHeading = true
                            outputStream.write(bLevelStyleToHeading[styleName])
                            outputStream.write(bSPACE)
                            break
                        }
                    }
                }
                case MdH1.NODE -> {
                    Integer styleLevelNum
                    if (minStyleLevelNum < MIN_LEVEL_CEILING && (styleLevelNum = nodeToStyleLevelNum[n])) {
                        isHeading = true
                        def hashCount = styleLevelNum - minStyleLevelNum + 1
                        assert hashCount > 0
                        outputStream.write((HASH * hashCount).getBytes(charset))
                        outputStream.write(bSPACE)
                    }
                }
                default -> println("** Unexpected mdLevelStyles: ${mdH1}")
            }
            String coreText
            String indent
            if (isHeading) {
                coreText = n.text
                indent = BLANK
            } else {
                (coreText, indent) = _mdGetPossiblyIndentedCoreTextAndIndent(n, ulOlStyles, nodeToStyles)
            }
            outputStream.write(coreText.getBytes(charset))
            outputStream.write(bNL)

            // process details and note
            [[settings.details, n.details?.plain], [settings.note, n.note?.plain]].each { tuple ->
                def (String mdInObj, String text) = tuple
                def mdIn = mdInObj as MdInclude
                if (mdIn != MdInclude.NONE && text) {
                    // add details/note as a separate paragraph
                    outputStream.write(bNL)
                    def processedText = switch (mdIn) {
                        case MdInclude.HLB -> _replaceNewLinesWithHardLineBreaks(text)
                        case MdInclude.PLAIN -> text
                        case MdInclude.QUOTE_HLB -> _quoteMdWithHardLineBreaks(text)
                        case MdInclude.QUOTE -> _quoteMd(text)
                        case MdInclude.CODE -> _codeMd(text)
                        default -> '#ERR!'
                    }
                    if (indent && processedText)
                        processedText = processedText.split(NL).collect { indent + it }.join(NL)
                    outputStream.write(processedText.getBytes(charset))
                    outputStream.write(bNL)
                }
            }
        }
    }

    /**
     * Special treatment for node-core with a single bullet point
     * if its format == Markdown or style is in [ulStyle, olStyle] (from settings)
     *
     * @return [ possibly indented core text, indent for details/note ]
     */
    static List<String> _mdGetPossiblyIndentedCoreTextAndIndent(Node n, List<String> ulOlStyles, Map<Node, List<String>> nodeToStyles) {
        String possiblyIndentedText
        int indentLevelForDetailsAndNote = 0
        def nText = n.text
        if (!nText) {
            possiblyIndentedText = nText
        } else {
            def (nIsUlBullet, nIsOlBullet) = ulOlStyles.collect { it in nodeToStyles[n] }
            if ((!nText.contains(NL) && (nIsUlBullet || nIsOlBullet))
                    || (n.format == MARKDOWN_FORMAT && nText ==~ RX_MD_LIST_ITEM)) {
                indentLevelForDetailsAndNote++
                for (def ancestor in n.pathToRoot.reverse().drop(1)) {
                    // only single-list-item nodes are considered
                    // i.e. Markdown with more lines won't work
                    def ancestorText = ancestor.text
                    if ((!ancestorText.contains(NL) && ulOlStyles.any { it in nodeToStyles[ancestor] })
                            || (ancestor.format == MARKDOWN_FORMAT && ancestorText ==~ RX_MD_LIST_ITEM))
                        indentLevelForDetailsAndNote++
                    else
                        break
                }
                def myIndent = TWO_SPACES * (indentLevelForDetailsAndNote - 1)
                def ulOlMark = nIsUlBullet ? '* ' : (nIsOlBullet ? '1. ' : BLANK)
                possiblyIndentedText = myIndent + ulOlMark + nText
            } else {
                possiblyIndentedText = nText
            }
        }
        def indentForDetailsAndNote = indentLevelForDetailsAndNote ? TWO_SPACES * indentLevelForDetailsAndNote : BLANK
        return [possiblyIndentedText, indentForDetailsAndNote]
    }

    static String _replaceNewLinesWithHardLineBreaks(String text) {
        text.replaceAll(RX_HARD_LINE_BREAK_CANDIDATE, BACKSLASH_NL)
    }

    static String _quoteMdWithHardLineBreaks(String text) {
        _replaceNewLinesWithHardLineBreaks(text.replaceAll(RX_MULTILINE_BEGINING, GT_SPACE))
    }

    static String _quoteMd(String text) {
        text.replaceAll(RX_MULTILINE_BEGINING, GT_SPACE)
    }

    static String _codeMd(String text) {
        text.replaceAll(RX_MULTILINE_BEGINING, FOUR_SPACES)
    }

    static List<List<Node>> createListOfRows(Node node, Integer numOfNodesToIgnore = 0) {
        node.find { it.leaf && it.visible }.collect {
            def eachNodeFromRootToIt = it.pathToRoot
            def i = eachNodeFromRootToIt.findIndexOf { it == node } + numOfNodesToIgnore
            eachNodeFromRootToIt[i..-1]
        }
    }

    static void toCsvFile(File file, Node node, HashMap<String, Object> settings = null) {
        def outputStream = new BufferedOutputStream(new FileOutputStream(file))
        toCsvOutputStream(outputStream, node, settings)
        outputStream.close()
    }

    static String toCsvString(Node node, HashMap<String, Object> settings = null) {
        def outputStream = new ByteArrayOutputStream()
        toCsvOutputStream(outputStream, node, settings)
        outputStream.toString(charset)
    }

    /**
     * Output to CSV, using any deilmeter as a separator (comma by default)
     *
     * @param outputStream the stream to write to
     * @param node the starting node for the export (see also settings.skip1)
     * @param settings a hashMap -- see csvSettings for default values =>
     *  sep -- separator to use;
     *  eol -- end of line to use;
     *  nl -- in-value new-line replacement (e.g. CR in place on NL);
     *  np -- NodePart to take the value from;
     *  skip1 -- whether to skip the first node;
     *  tail -- whether to put Separator after the last value;
     *  quote -- whether to force quotes around each value | default: auto-quote when sep or NL or CR in value;
     */
    static void toCsvOutputStream(OutputStream outputStream, Node node, HashMap<String, Object> settings) {
        settings = !settings ? csvSettings.clone() : csvSettings + settings
        def sep = settings.sep as String
        def eol = settings.eol as String
        def newlineReplacement = settings.getOrDefault('newlineReplacement', settings.nl) as String
        def nodePart = settings.getOrDefault('nodePart', settings.np) as NodePart
        def shouldQuote = settings.quote as boolean
        def skip1 = settings.skip1 as boolean
        def numOfNodesToIgnore = skip1 ? 1 : settings.getOrDefault('numOfNodesToIgnore', settings.getOrDefault('skip', 0)) as int
        def sepAtRowEnds = settings.getOrDefault('sepAtRowEnds', settings.tail) as boolean
        def sepAsBytes = sep.getBytes(charset)
        def rows = createListOfRows(node, numOfNodesToIgnore)
        def rowSizes = rows.collect { it.size() }
        def maxRowSize = rowSizes.max()
        rows.eachWithIndex { row, i ->
            if (sepAtRowEnds)
                outputStream.write(sepAsBytes)
            def rowSize = rowSizes[i]
            row.eachWithIndex { n, j ->
                def text = switch (nodePart) {
                    case NodePart.CORE -> HtmlUtils.htmlToPlain(n.transformedText)
                    case NodePart.DETAILS -> (n.details?.plain ?: '')
                    case NodePart.NOTE -> (n.note?.plain ?: '')
                    default -> '#ERR!'
                }
                if (newlineReplacement !== null)
                    text = text.replace(NL, newlineReplacement)
                if ((shouldQuote || text.contains(sep) || text.contains(NL) || text.contains(CR)) && sep != '"')
                    text = /"${text.replaceAll('"', '""')}"/
                outputStream.write(text.getBytes(charset))
                def isLastRow = j == rowSize - 1
                if (!isLastRow)
                    outputStream.write(sepAsBytes)
            }
            def delta = maxRowSize - rowSize
            (0..<delta).each { outputStream.write(sepAsBytes) }
            if (sepAtRowEnds)
                outputStream.write(sepAsBytes)
            outputStream.write(eol.getBytes(charset))
        }
    }

    static void toRumarTomlFile(File file, Node node) {
        def outputStream = new BufferedOutputStream(new FileOutputStream(file))
        toRumarTomlOutputStream(outputStream, node)
        outputStream.close()
    }

    static String toRumarTomlString(Node node) {
        def outputStream = new ByteArrayOutputStream()
        toRumarTomlOutputStream(outputStream, node)
        outputStream.toString(charset)
    }

    static void toRumarTomlOutputStream(OutputStream outputStream, Node node) {
        byte[] nlBytes = NL.getBytes(charset)
        GString text
        GString profile
        String entries
        List<Node> nChildren
        def allSettingNames = RUMAR_TOML_INTEGER_SETTINGS + RUMAR_TOML_BOOLEAN_SETTINGS + RUMAR_TOML_STRING_SETTINGS + RUMAR_TOML_ARRAY_SETTINGS
        node.children.each { n ->
            nChildren = n.children
            if (nChildren) {
                def nTextUncommented = n.text.replaceFirst(/^#/, '')
                if (nTextUncommented in allSettingNames) {
                    text = _toRumarTomlEntry(n)
                } else {
                    profile = "[${_quote(n.text)}]"
                    entries = nChildren.collect { _toRumarTomlEntry(it) }.join(NL)
                    text = "$NL$profile$NL$entries"
                }
                outputStream.write(text.getBytes(charset))
                outputStream.write(nlBytes)
            }
        }
    }

    static GString _toRumarTomlEntry(Node n) {
        def listOfRows = createListOfRows(n, 1)
        def nText = n.text
        def isCommented = nText.startsWith(HASH)
        def settingNameUncommented = isCommented ? nText.drop(1) : nText
        if (settingNameUncommented in RUMAR_TOML_INTEGER_SETTINGS || settingNameUncommented in RUMAR_TOML_BOOLEAN_SETTINGS) {
            "${nText} = ${listOfRows[0]*.text.join(BLANK)}"
        } else if (settingNameUncommented in RUMAR_TOML_STRING_SETTINGS) {
            "${nText} = ${_quote(listOfRows[0]*.text.join(BLANK))}"
        } else if (settingNameUncommented in RUMAR_TOML_ARRAY_SETTINGS) {
            def _h_ = isCommented ? HASH : BLANK
            "${nText} = [$NL${listOfRows.collect { row -> _h_ + FOUR_SPACES + _quote(row*.text.join(BLANK)) + COMMA }.join(NL)}$NL$_h_]"
        } else {
            throw IllegalArgumentException("${nText} not in RUMAR_TOML_*_SETTINGS in Export.groovy")
        }
    }

    static String _quoteTomlKeyIfNeeded(String text) {
        text ==~ RX_TOML_KEY ? text : _quote(text)
    }

    static String _quote(String text) {
        String quote
        if (SINGLE_QUOTE !in text)
            quote = SINGLE_QUOTE
        else if (DOUBLE_QUOTE !in text)
            quote = DOUBLE_QUOTE
        else if (MULTILINE_SINGLE_QUOTE !in text)
            quote = MULTILINE_SINGLE_QUOTE
        else if (MULTILINE_DOUBLE_QUOTE !in text)
            quote = MULTILINE_DOUBLE_QUOTE
        else
            throw new IllegalArgumentException("cannot quote `${text}` because it already contains all possible quote options")
        quote + text + quote
    }

    static void toJsonFile(File file, Node node, HashMap<String, Object> settings = null) {
        def jsonStr = toJsonString(node, settings)
        file.setText(jsonStr, charset.name())
    }

    /**
     * Export to JSON, in UTF-8 encoding, with the assumption that each node on the same level has a unique core text,
     * because it is used as Key in JSON
     * @param node - the starting node of the branch to be exported -- see also settings.skip1
     * @param settings - a hashMap -- see jsonSettings for default values
     *  - details -- whether to include details;
     *  - note -- whether to include note;
     *  - attributes -- whether to include attributes;
     *  - link -- whether to include link;
     *  - transformed -- whether to use transformed text, i.e. after formula/numbering/format evaluation;
     *  - style -- whether to include the individually-assigned style;
     *  - formatting -- whether to include formatting: backgroundColor, textColor;
     *  - icons -- whether to include icons;
     *  - skip1 -- whether to skip the first node;
     *  - denullify -- whether to try to avoid `{"Node text": null}` by replacing it with `"Node text"` where possible;
     *  - pretty -- whether to use pretty output format;
     *  - dateFmt -- how to format values of type Date: DISPLAYED, ISO_LOCAL, ISO;
     *  - format -- whether to include node/attribute format, details/note content type;
     *  - forceId -- whether to force the usage of node IDs as JSON keys (used regardless in case of non-unique siblings) and @core for core value;
     * @return JSON representation of the branch, in UTF-8 encoding
     */
    static String toJsonString(Node node, HashMap<String, Object> settings = null) {
        settings = !settings ? jsonSettings.clone() : jsonSettings + settings
        def forceId = settings.forceId as boolean
        def core = _toJson_calcCore(node, settings)
        def useAtCore = forceId || core instanceof List
        def mapOrList = _toJson_getBodyRecursively(node, settings, 1, useAtCore ? core : null)
        if (!settings.skip1)
            mapOrList = [(useAtCore ? node.id : core): mapOrList]
        if (settings.denullify) {
            // use a wrapper to also denullyfy top-level entries, so that top-level object can be a list
            mapOrList = _toJson_denullify(['x': mapOrList])['x']
        }
        def jsonPayload = new JsonGenerator.Options().disableUnicodeEscaping().build().toJson(mapOrList)
        if (settings.pretty)
            try {
                // since 4.0.19
                def disableUnicodeEscaping = true
                return JsonOutput.prettyPrint(jsonPayload, disableUnicodeEscaping)
            } catch (MissingMethodException ignore) {
                return JsonOutput.prettyPrint(jsonPayload)
            }
        else
            return jsonPayload
    }

    static HashMap<Object, Object> _toJson_getBodyRecursively(Node node, HashMap<String, Object> settings, int level = 1, core = null) {
        def details = settings.details ? (settings.transformed ? node.details?.text : HtmlUtils.htmlToPlain(node.detailsText ?: '')) : null
        def detailsContentType = settings.details && settings.format ? node.detailsContentType : null
        def note = settings.note ? (settings.transformed ? node.note?.text : HtmlUtils.htmlToPlain(node.noteText ?: '')) : null
        def noteContentType = settings.note && settings.format ? node.noteContentType : null
        def attributes = _toJson_getAttributes(node, settings)
        URI link = settings.link ? node.link.uri : null
        def style = settings.style ? node.style.name : null
        def formatting = settings.formatting as boolean
        def backgroundColor = formatting && node.style.isBackgroundColorSet() ? colorToRGBAString(node.style.backgroundColor) : null
        def textColor = formatting && node.style.isTextColorSet() ? colorToRGBAString(node.style.textColor) : null
        def icons = settings.icons ? node.icons.icons : Collections.emptyList()
        def children = node.children.findAll { it.visible }
        if (core === null && !details && !note && !attributes && !link && !style && !backgroundColor && !textColor && !icons && !children) {
            return null
        } else {
            def result = [:]
            if (!(level == 1 && settings.skip1)) {
                if (core !== null)
                    result[CORE] = core
                if (details)
                    result[DETAILS] = !settings.format || detailsContentType == AUTO_FORMAT ? details : [details, detailsContentType]
                if (note)
                    result[NOTE] = !settings.format || noteContentType == AUTO_FORMAT ? note : [note, noteContentType]
                if (attributes)
                    result[ATTRIBUTES] = attributes
                if (link)
                    result[LINK] = link.toString()
                if (style)
                    result[STYLE] = style
                if (icons)
                    result[ICONS] = icons
                if (backgroundColor)
                    result[BACKGROUND_COLOR] = backgroundColor
                if (textColor)
                    result[TEXT_COLOR] = textColor
            }
            def childToCalcCore = new HashMap<Node, Object>();
            children.each { childToCalcCore.put(it, _toJson_calcCore(it, settings)) }
            // use ID if `forceId: true` or core is not unique among children
            def useIdForChildren = settings.forceId || (children && children.size() != new HashSet<Object>(childToCalcCore.values()).size())
            Object childCore
            Object key
            Object atCore
            children.each { Node childNode ->
                childCore = childToCalcCore[childNode]
                assert childCore !== null
                // put core value either as key or as @core
                key = useIdForChildren || childCore instanceof List ? childNode.id : childCore
                atCore = useIdForChildren || childCore instanceof List ? childCore : null
                result[key] = _toJson_getBodyRecursively(childNode, settings, level + 1, atCore)
            }
            return result
        }
    }

    static _toJson_calcCore(Node node, Map<String, Object> settings) {
        def plainText = node.plainText
        def format = node.format
        if (!(plainText =~ RX_FORMULA)) {
            def conv = node.to
            if (conv.isNum()) {
                // always return a list, to indicate that @core must be used and avoid serialization to String (JSON keys must be String)
                def num = conv.num
                return !settings.format || format == STANDARD_FORMAT ? [num] : [num, node.object.class.simpleName, format]
            } else if (conv.isDate()) {
                def date = conv.date
                def pattern = (date as FormattedDate).pattern
                def value = toDateString(date, settings.dateFmt as DateFmt, format)
                // a date can be formatted twice: with node.format and (default) date pattern, so saving both
                return !settings.format ? value : [value, node.object.class.simpleName, format, pattern]
            }
        }
        // a formula or text
        def value = settings.transformed ? node.transformedText : plainText
        return !settings.format || format == STANDARD_FORMAT ? value : [value, node.object.class.simpleName, format]
    }

    /**
     *
     * @param date
     * @param dateFmt settings.dateFmt
     * @param pattern format used for display
     * @return
     */
    static String toDateString(Date date, DateFmt dateFmt, String pattern) {
        // Freeplane stores dates as FormattedDate, which extends Date and holds a text (originally entered),
        // and a pattern (SimpleDateFormat), which is used to format the date for attributes and for nodes if node.format is STANDARD_FORMAT, otherwise format is used.
        // node.to converts date to ConvertibleDate: this.date holds FormattedDate; this.text holds date formatted always as yyyy-MM-dd'T'HH:mmZ (probably never used).
        // ConvertibleDate.getDate() returns FormattedDate whose toString() returns df.format(date), where df is FormattedDate.pattern.
        // When a date is entered, pattern is set by Freeplane to the default date/time pattern from config.
        return switch (dateFmt) {
            case DateFmt.ISO_LOCAL -> toIsoLocalDate(date)
            case DateFmt.ISO -> date.toOffsetDateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            case DateFmt.DISPLAYED -> pattern ? fsbc.format(date, pattern) : date.toString()
        }
    }

    /**
     * ISO LOCAL date or date/time representation. Time is stripped if 00:00:00
     */
    static String toIsoLocalDate(Date date) {
        def dateTime = date.toLocalDateTime()
        DateTimeFormatter dateTimeFormatter
        if (dateTime.truncatedTo(ChronoUnit.DAYS) == dateTime)
            dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        else
            dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        return dateTime.format(dateTimeFormatter)
    }

    /** Gets attributes in the simplest possible format: a map or a list of lists – if there is more
     * than one attribute with the same name or any of the node's attributes has a pattern.
     * An attribute can be a string, number or date and can have a pattern (can be formatted).
     * A formula can result in a string, number or date.
     * The value of a transformed attribute (evaluated formula) returned by the API is raw;
     * NB. Date is represented as Date#toString(). To get the value as displayed by Freeplane, one needs to apply
     * the pattern for the non-transformed attribute if such pattern is present.
     */
    static Object _toJson_getAttributes(Node node, Map<String, Object> settings) {
        // Note: transformed attributes loose format information -> use raw attribute's format
        def dateFmt = settings.dateFmt as DateFmt
        def attributes = node.attributes
        def attributeKeyToCount = [:]
        def attributesList = new ArrayList<List<Object>>(attributes.size())
        attributes.each { Map.Entry entry ->
            attributeKeyToCount.compute(entry.key) { key, value -> value ? ++value : 1 }
            def entryList = new ArrayList<Object>(4)
            entryList << entry.key
            if (entry.value instanceof FormattedDate) {
                def value = entry.value as FormattedDate
                entryList << toDateString(value, dateFmt, value.pattern)
                entryList << value.class.simpleName
                entryList << value.pattern
            } else if (entry.value instanceof Hyperlink || entry.value instanceof URI) {
                entryList << entry.value.toString()
                entryList << entry.value.class.simpleName
            } else if (entry.value instanceof FormattedNumber) {
                def value = entry.value as FormattedNumber
                entryList << value.number
                entryList << value.class.simpleName
                entryList << value.pattern
            } else if (entry.value instanceof FormattedFormula) {
                // formula can result in a string, number or date
                def value = entry.value as FormattedFormula
                entryList << value.object
                entryList << value.class.simpleName
                entryList << value.pattern
            } else if (entry.value instanceof FormattedObject) {
                // occurs when a number-with-format attrib is set a text value
                def value = entry.value as FormattedObject
                entryList << value.object
                entryList << value.class.simpleName
                entryList << value.pattern
            } else {
                entryList << entry.value
            }
            attributesList << entryList
        }
        // Use transformed value if requested
        // Convertible is the result of a formula accessing another attribute's value: node['key']
        // FormattedDate is when accessing another attribute's value which is a date (but not a formula)
        if (settings.transformed) {
            attributes.transformed.eachWithIndex { Map.Entry entry, int i ->
                def value = entry.value
                def entryList = attributesList[i]
                if (value != entryList[1]) {
                    def entryListSize = entryList.size()
                    if (value instanceof ConvertibleText) {
                        value = value.text
                    } else if (value instanceof ConvertibleNumber) {
                        value = value.num
                    } else if (value instanceof Date) {
                        // this covers ConvertibleDate and FormattedDate
                        String pattern = null
                        if (value instanceof FormattedDate) {
                            // Freeplane displays the result of an evaluated formula using a date pattern from upstream
                            pattern = value.pattern
                            if (settings.format) {
                                if (entryListSize < 3) {
                                    // reflect the type of the original formula (i.e. String)
                                    attributesList[i][2] = entryList[1].class.simpleName
                                }
                                attributesList[i][3] = pattern
                            }
                        } else if (entryListSize >= 4) {
                            pattern = entryList[3]
                        }
                        value = toDateString(value, dateFmt, pattern)
                    }
                    // update value
                    attributesList[i][1] = value
                }
            }
        }
        // remove type and pattern if not requested
        if (!settings.format) {
            attributesList.each { entryList ->
                def size = entryList.size()
                if (size >= 4)
                    entryList.remove(3)
                if (size >= 3)
                    entryList.remove(2)
            }
        }
        // Use a (simple) map if format not requested and no duplicate keys
        if (!settings.format && attributeKeyToCount.values().every { it == 1 }) {
            return attributesList.collectEntries()
        } else {
            return attributesList
        }
    }

    static HashMap<Object, Object> _toJson_denullify(HashMap<String, Object> hashMap) {
        def newHashMap = [:]
        hashMap.each { k, v ->
            if (v instanceof HashMap) {
                if (v.every { it.value === null }) {
                    def lst = v.collect { it.key }
                    newHashMap[k] = lst.size() == 1 ? lst[0] : lst
                } else {
                    newHashMap[k] = _toJson_denullify(v)
                }
            } else {
                newHashMap[k] = v
            }
        }
        return newHashMap
    }
}
