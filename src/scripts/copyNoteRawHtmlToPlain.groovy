// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Copy"})
import org.freeplane.core.util.HtmlUtils
import org.freeplane.core.util.TextUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

/*
 * Despite the name, noteText is the raw HTML
 * Additionally, there's unexposed org.freeplane.plugin.script.proxy.ConvertibleHtmlText.getHtml
 * therefore node.note.html will also work, though IDE won't get its type
 */
def node = ScriptUtils.node()
def c = ScriptUtils.c()
def text = node.noteText
if (text) {
    TextUtils.copyToClipboard(HtmlUtils.htmlToPlain(text))
} else {
    c.statusInfo = "cannot copy note because it's missing"
}
