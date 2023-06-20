// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Copy"})
import org.freeplane.core.util.TextUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

/*
 * ConvertibleNoteText extends ConvertibleHtmlText, whose constructor calls
 * super(FormulaUtils.safeEvalIfScript(nodeModel, htmlToPlain(htmlText)));
 * and the plain text ends up in this.text
 */
def node = ScriptUtils.node()
def c = ScriptUtils.c()
if (node.note) {
    TextUtils.copyToClipboard(node.note.text)  // equivalent to HtmlUtils.htmlToPlain(node.note.string | node.note.html)), i.e. with formula evaluation
} else {
    c.statusInfo = "cannot copy note because it's missing"
}
