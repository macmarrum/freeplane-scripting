/*
 * Copyright (C) 2023, 2024  macmarrum
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
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Paste"})

import groovy.xml.XmlSlurper
import org.freeplane.api.Node
import org.freeplane.core.util.HtmlUtils
import org.freeplane.features.map.clipboard.MapClipboardController
import org.freeplane.features.map.clipboard.MindMapNodesSelection
import org.freeplane.plugin.script.proxy.ScriptUtils

import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable

/** Paste content from Transferable (clipboard).
 *  The content might be
 *  - Freeplane node(s)
 *  - HTML: full or partial
 *  - plain text
 *  In case of HTML, when note's Format is Markdown or Text,
 *  `<html><body>` and `</body><html>` is removed (if present),
 *  to avoid auto-parsing of html by Freeplane, i.e. so that the content is pasted as is.
 *  Otherwise `<html><body>` and `</body><html>` is kept or -- in the case of partial html -- added.
 */
final SCRIPT_NAME = 'pasteToNote'
def node = ScriptUtils.node()
//[MarkdownRenderer.MARKDOWN_CONTENT_TYPE, TextController.CONTENT_TYPE_HTML])
def isOutcomeToContainTagsHtmlBody = node.noteContentType !in ['markdown', 'html']
def transferable = Toolkit.defaultToolkit.systemClipboard.getContents(null)
def text = getString(transferable, isOutcomeToContainTagsHtmlBody, SCRIPT_NAME)
if (text)
    node.noteText = text

private static String getString(Transferable t, boolean isOutcomeToContainHtmlBody, String scriptName) {
    def c = ScriptUtils.c()
    if (t.isDataFlavorSupported(MindMapNodesSelection.mindMapNodesFlavor)) {
        // a list of nodes => assume concatenation of core text is to be done
        try {
            def xml = t.getTransferData(MindMapNodesSelection.mindMapNodesFlavor).toString()
            def nodes = getNodesFromClipboardXml(xml)
            final nodeCount = nodes.size()
            if (nodeCount == 0)
                return null
            def listOfTextToIsHtml = new ArrayList<Map.Entry<String, Boolean>>(nodeCount)
            nodes.each {
                def text = it.text
                def entry = new AbstractMap.SimpleEntry<String, Boolean>(text, HtmlUtils.isHtml(text))
                listOfTextToIsHtml << entry
            }
            def sb = new StringBuilder()
            if (listOfTextToIsHtml.any { it.value }) {
                // there is at least 1 html entry
                if (isOutcomeToContainHtmlBody)
                    sb << '<html><body>'
                listOfTextToIsHtml.eachWithIndex { it, i ->
                    if (it.value) { // isHtml
                        sb << it.key.replaceAll($/\s*(<html>|<body>|</body>|</html>)\s*/$, '')
                        sb << '\n'
                    } else {
                        it.key.split(/\n/).each {
                            sb << '<p>' << it << '</p>'
                            sb << '\n'
                        }
                    }
                }
                if (isOutcomeToContainHtmlBody)
                    sb << '</body></html>'
            } else {
                // no html entries
                listOfTextToIsHtml.eachWithIndex { it, i ->
                    sb << it.key
                    if (i < nodeCount - 1)
                        sb << '\n'
                }
            }
            return sb
        } catch (ignored) {
        }
    } else if (t.isDataFlavorSupported(DataFlavor.allHtmlFlavor)) {
        try {
            def html = t.getTransferData(DataFlavor.allHtmlFlavor).toString()
            html = html.replaceFirst($/(?i)^<!DOCTYPE html>\n*/$, '')
            if (isOutcomeToContainHtmlBody) {
                if (HtmlUtils.isHtml(html)) {
                    // full html => only remove head, as Freeplane doesn't use it
                    return html.replaceAll($/(?s)\s*<head>.*</head>\s*/$, '')
                } else {
                    // partial html => add tags so that Freeplane can auto-parse html
                    return "<html><body>$html</body></html>"
                }
            } else {
                if (HtmlUtils.isHtml(html)) {
                    // full html => get rid of head (element) and html & body (tags)
                    return html.replaceAll($/(?s)\s*<head>.*</head>\s*/$, '')
                            .replaceAll($/\s*(<html>|<body>|</body>|</html>)\s*/$, '')
                } else {
                    // partial html
                    return html
                }
            }
        } catch (ignored) {
        }
    } else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        try {
            def text = t.getTransferData(DataFlavor.stringFlavor).toString()
            // in case the text is HTML code, remove doctype
            text = text.replaceFirst($/(?i)^<!DOCTYPE html>\n*/$, '')
            if (isOutcomeToContainHtmlBody) {
                if (HtmlUtils.isHtml(text)) {
                    // full html => only remove head, as Freeplane doesn't use it
                    return text.replaceAll($/(?s)\s*<head>.*</head>\s*/$, '')
                } else {
                    // partial or no html => return as is
                    return text
                }
            } else {
                if (HtmlUtils.isHtml(text)) {
                    // full html => get rid of head (element) and html & body (tags)
                    return text.replaceAll($/(?s)\s*<head>.*</head>\s*/$, '')
                            .replaceAll($/\s*(<html>|<body>|</body>|</html>)\s*/$, '')
                } else {
                    // partial or no html => return as is
                    return text
                }
            }
        } catch (ignored) {
        }
    }
//    println(t.transferDataFlavors.collect(new HashSet<String>()) { it.mimeType.split(';')[0] })
    c.statusInfo = "$scriptName: error getting clipboard contents"
    return null
}

static java.util.List<Node> getNodesFromClipboardXml(String xml) {
    def parser = new XmlSlurper()
    def mindMap = ScriptUtils.node().mindMap
    try {
        return xml.split(MapClipboardController.NODESEPARATOR).collect { String xmlSingleNode ->
            // replace &nbsp; to avoid the error: nbsp was referenced but not declared
            xmlSingleNode = xmlSingleNode.replaceAll('&nbsp;', ' ')
            def xmlRootNode = parser.parseText(xmlSingleNode)
            mindMap.node(xmlRootNode.@ID as String)
        }
    } catch (ignored) {
    }
    return []
}
