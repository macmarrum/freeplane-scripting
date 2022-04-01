// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
import org.freeplane.core.util.TextUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

/*
 * ConvertibleHtmlText's constructor calls
 * super(FormulaUtils.safeEvalIfScript(nodeModel, htmlToPlain(htmlText)));
 * and the plain text ends up in this.text
 */
def node = ScriptUtils.node()
def c = ScriptUtils.c()
if (node.details)
    TextUtils.copyToClipboard(node.details.text)  // equivalent to HtmlUtils.htmlToPlain(node.details.string | node.details.html)), i.e. with formula evaluation
else {
    c.statusInfo = "cannot copy details because it's missing"
}
