// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
import org.freeplane.core.util.HtmlUtils
import org.freeplane.core.util.TextUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

/*
 * Despite the name, detailsText is the raw HTML
 * Additionally, there's unexposed org.freeplane.plugin.script.proxy.ConvertibleHtmlText.getHtml
 * therefore node.details.html will also work, though IDE won't get its type
 */
def node = ScriptUtils.node()
def c = ScriptUtils.c()
if (node.details)
    TextUtils.copyToClipboard(HtmlUtils.htmlToPlain(node.detailsText))
else {
    c.statusInfo = "cannot copy details because it's missing"
}
