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
import org.freeplane.core.util.TextUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

/*
 * ConvertibleHtmlText's constructor calls
 * super(FormulaUtils.safeEvalIfScript(nodeModel, htmlToPlain(htmlText)));
 * and the plain text ends up in this.text
 */
def c = ScriptUtils.c()
def selecteds = c.selecteds
def lst = new ArrayList<String>(selecteds.size())
String text
selecteds.each {
    text = it.details?.text // equivalent to HtmlUtils.htmlToPlain(node.details.string | node.details.html)), i.e. with formula evaluation
    if (text)
        lst << text
}
if (lst) {
    TextUtils.copyToClipboard(lst.join('\n'))
} else {
    c.statusInfo = "cannot copy details because it's missing"
}
