/*
 * Copyright (C) 2022-2024  macmarrum (at) outlook (dot) ie
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
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Copy"})

import org.freeplane.core.util.HtmlUtils
import org.freeplane.core.util.TextUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

def c = ScriptUtils.c()
final selecteds = c.selecteds
final nodeCount = selecteds.size()
def listOfTextToIsHtml = new ArrayList<Map.Entry<String, Boolean>>(nodeCount)
String text
Map.Entry<String, Boolean> entry
selecteds.each {
    text = it.text
    entry = new AbstractMap.SimpleEntry<>(text, HtmlUtils.isHtml(text))
    listOfTextToIsHtml << entry
}
def sb = new StringBuilder()
if (listOfTextToIsHtml.any { it.value }) {
    // there is at least 1 HTML entry
    sb << '<html><body>'
    listOfTextToIsHtml.eachWithIndex { it, i ->
        if (it.value) { // is HTML
            sb << it.key.replaceFirst($/(?si)\s*<head>.*</head>\s*/$, '')
                    .replaceAll($/(?i)\s*(<html>|<body>|</body>|</html>)\s*/$, '')
            sb << '\n'
        } else { // is not HTML
            it.key.split(/\n/).each {
                sb << '<p>' << it << '</p>'
                sb << '\n'
            }
        }
    }
    sb << '</body></html>'
} else {
    // no HTML entries
    listOfTextToIsHtml.eachWithIndex { it, i ->
        sb << it.key
        if (i < nodeCount - 1)
            sb << '\n'
    }
}
TextUtils.copyToClipboard(sb as String)
