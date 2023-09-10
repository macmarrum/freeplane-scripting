// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Paste"})


import groovy.xml.XmlParser
import org.freeplane.api.Node
import org.freeplane.core.util.FreeplaneVersion
import org.freeplane.core.util.HtmlUtils
import org.freeplane.features.map.clipboard.MapClipboardController
import org.freeplane.features.map.clipboard.MindMapNodesSelection
import org.freeplane.plugin.script.proxy.ScriptUtils

import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable

/** Paste HTML or String from Transferable (clipboard).
 *  If the content starts with <html> and ContentType aka Format is Markdown or Text/NoFormat/Html,
 *  add a space/apostrophe as the first character. This will prevent Freeplane from rewriting the HTML.
 */
def t = Toolkit.defaultToolkit.systemClipboard.getContents(null)
def node = ScriptUtils.node()
def isTargetMarkdownOrText = node.format in ['markdownPatternFormat', 'NO_FORMAT'] // [MarkdownRenderer.MARKDOWN_FORMAT, PatternFormat.IDENTITY_PATTERN]
def shouldOutcomeContainTagsHtmlBody = !isTargetMarkdownOrText
//def shouldOutcomeContainHtmlBody = true
def text = getString(t, shouldOutcomeContainTagsHtmlBody)
if (text) {
    if (shouldOutcomeContainTagsHtmlBody && isTargetMarkdownOrText && HtmlUtils.isHtml(text))
        text = (FreeplaneVersion.version < FreeplaneVersion.getVersion('1.11.1') ? /'/ : ' ') + text
    node.text = text
}

private static String getString(Transferable t, shouldOutcomeContainHtmlBody = false) {
    def c = ScriptUtils.c()
    if (t.isDataFlavorSupported(MindMapNodesSelection.mindMapNodesFlavor)) {
        try {
            def xml = t.getTransferData(MindMapNodesSelection.mindMapNodesFlavor).toString()
            def nodes = getNodesFromClipboardXml(xml)
            return nodes.collect { it.text }.join('\n')
        } catch (ignored) {
        }
    } else if (t.isDataFlavorSupported(DataFlavor.allHtmlFlavor)) {
        try {
            def html = t.getTransferData(DataFlavor.allHtmlFlavor).toString()
            html = html.replaceFirst($/(?i)^<!DOCTYPE html>\n*/$, '')
            if (shouldOutcomeContainHtmlBody) {
                if (HtmlUtils.isHtml(html)) {
                    return html.replaceAll($/(?s)<head>.*</head>/$, '')
                } else {
                    return "<html><body>$html</body></html>"
                }
            } else {
                if (HtmlUtils.isHtml(html))
                    return html.replaceAll($/(?s)<head>.*</head>\n*/$, '').replaceAll($/(<html>|<body>|</body>|</html>)/$, '')
                else
                    return html
            }
        } catch (ignored) {
        }
    } else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        try {
            return t.getTransferData(DataFlavor.stringFlavor).toString()
        } catch (ignored) {
        }
    }
//    println(t.transferDataFlavors.collect(new HashSet<String>()) { it.mimeType.split(';')[0] })
    c.statusInfo = 'pasteToCore: error getting clipboard contents'
    return null
}

static java.util.List<Node> getNodesFromClipboardXml(String xml) {
    Node node = ScriptUtils.node()
    try {
        def parser = new XmlParser()
        return xml.split(MapClipboardController.NODESEPARATOR).collect { String xmlSingleNode ->
            // replace &nbsp; to avoid the error: nbsp was referenced but not declared
            def xmlRootNode = parser.parseText(xmlSingleNode.replaceAll('&nbsp;', ' '))
            node.mindMap.node(xmlRootNode.@ID)
        }
    } catch (ignored) {
    }
    return []
}
