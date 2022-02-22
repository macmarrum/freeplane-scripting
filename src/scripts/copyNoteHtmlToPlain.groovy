// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
import org.freeplane.core.util.HtmlUtils
if (node.note)
    textUtils.copyToClipboard(HtmlUtils.htmlToPlain(node.note.html))
else
    c.statusInfo = /cannot copy note because it's missing/
