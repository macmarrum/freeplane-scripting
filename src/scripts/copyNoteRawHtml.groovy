/*
 * Copyright (C) 2022-2024  macmarrum
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

import org.freeplane.core.util.TextUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

/*
 * Despite the name, noteText is the raw HTML
 * Additionally, there's unexposed org.freeplane.plugin.script.proxy.ConvertibleHtmlText.getHtml
 * therefore node.note.html will also work, though IDE won't get its type
 */
def c = ScriptUtils.c()
final selecteds = c.selecteds
def lst = new ArrayList<String>(selecteds.size() + 2)
String text
selecteds.each {
    text = it.noteText.replaceFirst($/(?si)\s*<head>.*</head>\s*/$, '')
            .replaceAll($/(?i)\s*(<html>|<body>|</body>|</html>)\s*/$, '')
    if (text)
        lst << text
}
if (lst) {
    lst.add(0,'<html><body>')
    lst << '</body></html>'
    TextUtils.copyToClipboard(lst.join('\n'))
} else {
    c.statusInfo = "cannot copy note because it's missing"
}
