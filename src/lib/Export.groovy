package io.github.macmarrum.freeplane

import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.freeplane.core.util.HtmlUtils

import javax.swing.JFileChooser
import javax.swing.JOptionPane
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

class Export {
    public static Charset charset = StandardCharsets.UTF_8
    public static final String COMMA = ','
    public static final String TAB = '\t'
    public static final String NL = '\n'
    public static final String CR = '\r'
    public static final String SPACE = ' '
    public static final String HASH = '#'
    public static final Pattern RX_MULTILINE_BEGINING = ~/(?m)^/
    public static final String GT_SPACE = '> '
    public static final String FOUR_SPACES = '    '
    public static final Pattern RX_HARD_LINE_BREAK_CANDIDATE = ~/(?<!^|\\|\n)\n(?!\n|$)/
    public static final String BACKSLASH_NL = '\\\\\n'
    public static final Pattern RX_AUTOMATIC_LAYOUT_LEVEL = ~/^AutomaticLayout.level(,|\.)/
    public static final String BLANK = ''
    public static final String ROOT = 'root'
    public static final String ZERO = '0'
    public static final Integer MIN_LEVEL_CEILING = 999
    public static levelStyleToMdHeading = [
            'AutomaticLayout.level.root': '#',
            'AutomaticLayout.level,1'   : '##',
            'AutomaticLayout.level,2'   : '###',
            'AutomaticLayout.level,3'   : '####',
            'AutomaticLayout.level,4'   : '#####',
            'AutomaticLayout.level,5'   : '######',
            'AutomaticLayout.level,6'   : '#######',
    ]
    public static mdSettings = [levelStyles: MdLevelStyles.REL, details: MdInclude.HLB, note: MdInclude.PLAIN]
    public static csvSettings = [sep: COMMA, eol: NL, newlineReplacement: CR, nodePart: NodePart.CORE]

    enum NodePart {
        CORE, DETAILS, NOTE
    }

    /**
     * <p>Markdown Inclusion</p>
     * <p>Reflects how input should be treated in Markdown</p>
     * <ul>
     *     <li>NONE - not included</li>
     *     <li>QUOTE - as a quote; lines will be merged</li>
     *     <li>HLB - hard line breaks for each line</li>
     *     <li>QTHLB - as a quote with a hard line break for each line</li>
     *     <li>CODE - as a code block</li>
     *     <li>PLAIN - plain text, without any modification</li>
     * </ul>
     */
    enum MdInclude {
        NONE, QUOTE, HLB, QTHLB, CODE, PLAIN
    }

    /**
     * ABS - absolute, i.e. root: #, Level 1: ##, etc.
     * REL - relative, i.e. the first encountered Level Style: #, the second: ##, etc
     */
    enum MdLevelStyles {
        NONE, ABS, REL
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

    static String toMarkdownOutputStream(OutputStream outputStream, Node node, HashMap<String, Object> settings = null) {
        settings = !settings ? mdSettings.clone() : mdSettings + settings
        def nlBytes = NL.getBytes(charset)
        def spaceBytes = SPACE.getBytes(charset)
        def mdLevelStyles = settings.levelStyles as MdLevelStyles
        def levelStyleToMdHeadingBytes
        if (mdLevelStyles == MdLevelStyles.ABS) {
            levelStyleToMdHeadingBytes = levelStyleToMdHeading.collectEntries { k, v -> [k, v.getBytes(charset)] }
        }
        def nodeToStyles = new LinkedHashMap<Node, List<String>>()
        node.find { it.visible }.each { nodeToStyles[it] = it.style.allActiveStyles }
        Integer minStyleLevelNum = MIN_LEVEL_CEILING
        def nodeToStyleLevelNum = new HashMap<Node, Integer>()
        if (mdLevelStyles == MdLevelStyles.REL) {
            nodeToStyles.each { n, allActiveStyles ->
                def assignedLevelStyle = allActiveStyles.find { it in levelStyleToMdHeading }
                if (assignedLevelStyle) {
                    def styleLevelStr = assignedLevelStyle.replaceAll(RX_AUTOMATIC_LAYOUT_LEVEL, BLANK).replace(ROOT, ZERO)
                    def styleLevelNum = styleLevelStr as Integer
                    nodeToStyleLevelNum[n] = styleLevelNum
                    if (styleLevelNum < minStyleLevelNum)
                        minStyleLevelNum = styleLevelNum
                }
            }
        }
        nodeToStyles.each { n, allActiveStyles ->
            boolean isHeading = false
            switch (mdLevelStyles) {
                case MdLevelStyles.ABS -> {
                    for (def styleName in allActiveStyles) {
                        if (levelStyleToMdHeadingBytes.containsKey(styleName)) {
                            isHeading = true
                            if (!n.root) outputStream.write(nlBytes)
                            outputStream.write(levelStyleToMdHeadingBytes[styleName])
                            outputStream.write(spaceBytes)
                            break
                        }
                    }
                }
                case MdLevelStyles.REL -> {
//                    println(":: minLevel: ${minLevel}")
                    Integer styleLevelNum
                    if (minStyleLevelNum < MIN_LEVEL_CEILING && (styleLevelNum = nodeToStyleLevelNum[n])) {
                        isHeading = true
                        if (!n.root) outputStream.write(nlBytes)
                        def hashCount = styleLevelNum - minStyleLevelNum + 1
                        assert hashCount > 0
                        outputStream.write((HASH * hashCount).getBytes(charset))
                        outputStream.write(spaceBytes)
                    }
                }
                default -> println("** Unexpected mdLevelStyles: ${mdLevelStyles}")
            }
            if (!isHeading) outputStream.write(nlBytes)
            outputStream.write(n.text.getBytes(charset))
            outputStream.write(nlBytes)

            // process details and note
            [[settings.details, n.details?.plain], [settings.note, n.note?.plain]].each { tuple ->
                def (mdInObj, text) = tuple
                def mdIn = mdInObj as MdInclude
                if (mdIn != MdInclude.NONE && text) {
                    // add details/note as a seprate paragraph
                    outputStream.write(nlBytes)
                    def processedText = switch (mdIn) {
                        case MdInclude.HLB -> _replaceNewLinesWithHardLineBreaks(text)
                        case MdInclude.PLAIN -> text
                        case MdInclude.QTHLB -> _quoteMdWithHardLineBreaks(text)
                        case MdInclude.QUOTE -> _quoteMd(text)
                        case MdInclude.CODE -> _codeMd(text)
                        default -> '#ERR!'
                    }
                    outputStream.write(processedText.getBytes(charset))
                    outputStream.write(nlBytes)
                }
            }
        }
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

    static List<List<Node>> createListOfRows(Node node) {
        node.find { it.leaf && it.visible }.collect {
            def eachNodeFromRootToIt = it.pathToRoot
            def i = eachNodeFromRootToIt.findIndexOf { it == node }
            eachNodeFromRootToIt[i..-1]
        }
    }

    static void toCsvFile(File file, Node node, HashMap<String, Object> settings) {
        def outputStream = new BufferedOutputStream(new FileOutputStream(file))
        toCsvOutputStream(outputStream, node, settings)
        outputStream.close()
    }

    static String toCsvString(Node node, HashMap<String, Object> settings) {
        def outputStream = new ByteArrayOutputStream()
        toCsvOutputStream(outputStream, node, settings)
        outputStream.toString(charset)
    }


    static void toCsvOutputStream(OutputStream outputStream, Node node, HashMap<String, Object> settings) {
        settings = !settings ? csvSettings.clone() : csvSettings + settings
        def sep = settings.sep as String
        def eol = settings.eol as String
        def newlineReplacement = settings.newlineReplacement as String
        def nodePart = settings.nodePart as NodePart
        def sepAsBytes = sep.getBytes(charset)
        def rows = createListOfRows(node)
        def rowSizes = rows.collect { it.size() }
        def maxRowSize = rowSizes.max()
        rows.eachWithIndex { row, i ->
            def rowSize = rowSizes[i]
            row.eachWithIndex { n, j ->
                def text = switch (nodePart) {
                    case NodePart.CORE -> HtmlUtils.htmlToPlain(n.transformedText)
                    case NodePart.DETAILS -> (n.details?.plain ?: '')
                    case NodePart.NOTE -> (n.note?.plain ?: '')
                    default -> '#ERR!'
                }
                if (text.contains(sep) && sep != '"')
                    text = /"$text"/
                if (newlineReplacement !== null)
                    text = text.replace(NL, newlineReplacement)
                outputStream.write(text.getBytes(charset))
                def isLastRow = j == rowSize - 1
                if (!isLastRow)
                    outputStream.write(sepAsBytes)
            }
            def delta = maxRowSize - rowSize
            (0..<delta).each { outputStream.write(sepAsBytes) }
            outputStream.write(eol.getBytes(charset))
        }
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
            def message = "${file.path} exists.\nOverwrite?"
            def title = 'Confirm overwrite'
            def decision = UITools.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
            if (decision != JOptionPane.YES_OPTION)
                return
        }
        return file
    }
}
