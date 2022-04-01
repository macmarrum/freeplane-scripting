// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
import org.freeplane.core.util.TextUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

/*
 * Despite the name, noteText is the raw HTML
 * Additionally, there's unexposed org.freeplane.plugin.script.proxy.ConvertibleHtmlText.getHtml
 * therefore node.note.html will also work, though IDE won't get its type
 */
def node = ScriptUtils.node()
def c = ScriptUtils.c()
if (node.note)
    TextUtils.copyToClipboard(node.noteText)
else {
    c.statusInfo = "cannot copy note because it's missing"
}
