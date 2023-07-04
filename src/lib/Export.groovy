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
        parent.findAll().each {
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
}
