package io.github.macmarrum.freeplane

import org.freeplane.api.Node
import org.freeplane.core.util.HtmlUtils

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class Export {
    private static String UTF8 = StandardCharsets.UTF_8.name()
    public static final COMMA = ','
    public static final String NL = '\n'
    public static final String CR = '\r'

    enum NodePart {
        CORE, DETAILS, NOTE
    }


    static void exportMarkdownLevelStyles(File file, Node parent) {
        def text = createMarkdownLevelStyles(parent)
        file.setText(text, UTF8)
    }

    /**
     * https://github.com/freeplane/freeplane/issues/333
     */
    static String createMarkdownLevelStyles(Node parent) {
        def map = [
                'AutomaticLayout.level.root': '#',
                'AutomaticLayout.level,1'   : '##',
                'AutomaticLayout.level,2'   : '###',
                'AutomaticLayout.level,3'   : '####',
                'AutomaticLayout.level,4'   : '#####',
                'AutomaticLayout.level,5'   : '######',
                'AutomaticLayout.level,6'   : '#######',
        ]
        def levelStyles = map.keySet()
        def sb = new StringBuilder()
        parent.find { it.visible }.each {
            boolean isHeading = false
            for (String styleName in it.style.allActiveStyles) {
                if (styleName in levelStyles) {
                    isHeading = true
                    if (!it.root) sb << NL
                    sb << map[styleName] << ' '
                    break
                }
            }
            if (!isHeading) sb << NL
            sb << it.text << NL
            if (it.detailsText) {
                sb << it.details.text << NL
            }
        }
        return sb.toString()
    }

    static List<List<Node>> createListOfRows(Node node) {
        return node.find { it.leaf && it.visible }.collect {
            def eachNodeFromRootToIt = it.pathToRoot
            def i = eachNodeFromRootToIt.findIndexOf { it == node }
            eachNodeFromRootToIt[i..-1]
        }
    }

    static void exportCsv(File file, Node node, String sep = COMMA, String eol = NL) {
        exportCsvNodePart(NodePart.CORE, file, node, sep, eol)
    }

    static void exportCsvNodePart(NodePart nodePart, File file, Node node, String sep = COMMA, String eol = NL, String newLineReplacement = null) {
        def rows = createListOfRows(node)
        def rowSizes = rows.collect { it.size() }
        def maxRowSize = rowSizes.max()
        file.withPrintWriter(UTF8) { pw ->
            rows.eachWithIndex { row, i ->
                def rowSize = rowSizes[i]
                row.eachWithIndex { Node n, int j ->
                    def text = switch (nodePart) {
                        case NodePart.CORE -> HtmlUtils.htmlToPlain(n.transformedText)
                        case NodePart.DETAILS -> (n.details?.text ?: '')
                        case NodePart.NOTE -> (n.note?.text ?: '')
                        default -> '#ERR!'
                    }
                    if (text.contains(sep) && sep != '"')
                        text = /"$text"/
                    if (newLineReplacement !== null)
                        text = text.replace(NL, newLineReplacement)
                    pw.print(text)
                    def isLastRow = j == rowSize - 1
                    if (!isLastRow)
                        pw.print(sep)
                }
                def delta = maxRowSize - rowSize
                (0..<delta).each { pw.print(sep) }
                pw.print(eol)
            }
        }
    }

    static String createCsv(Node node, String sep = COMMA, String eol = NL) {
        def rows = createListOfRows(node)
        def maxRowSize = rows.collect { it.size() }.max()
        return rows.collect { row ->
            def line = row.collect {
                def text = it.transformedText
                if (sep != '"' && text.contains(sep))
                    text = /"$text"/
                return text
            }.join(sep)
            int delta = maxRowSize - row.size()
            if (delta)
                line += sep * delta
            return line
        }.join(eol)
    }
}
