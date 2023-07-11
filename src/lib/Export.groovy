package io.github.macmarrum.freeplane

import org.freeplane.api.Node

import java.nio.charset.StandardCharsets

class Export {
    private static String UTF8 = StandardCharsets.UTF_8.name()

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
                    if (!it.root) sb << '\n'
                    sb << map[styleName] << ' '
                    break
                }
            }
            if (!isHeading) sb << '\n'
            sb << it.text << '\n'
            if (it.detailsText) {
                sb << it.details.text << '\n'
            }
        }
        return sb.toString()
    }

    static void exportCsv(File file, Node node, String sep = ',', String eol = '\n') {
        def text = createCsv(node)
        file.setText(text, UTF8)
    }

    static String createCsv(Node node, String sep = ',', String eol = '\n') {
        def rows = node.find { it.leaf && it.visible }.collect {
            def ptr = it.pathToRoot
            def i = ptr.findIndexOf { it == node }
            ptr[i..-1]
        }
        def maxRowSize = rows.collect { it.size() }.max()
        rows.each { row ->
            def delta = maxRowSize - row.size()
            (0..<delta).each {
                row << ''
            }
        }
        return rows.collect { row ->
            row.collect {
                try {
                    it.transformedText
                } catch (Exception ex) {
                    println("** error while processing `${it}`")
                    throw ex
                }
            }.join(sep)
        }.join(eol)
    }
}
