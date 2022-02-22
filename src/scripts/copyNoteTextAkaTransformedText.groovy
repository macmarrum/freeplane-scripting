// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
import org.freeplane.api.Node as FPN
if (node.note)
    textUtils.copyToClipboard(node.note.text)  //same as .plain
else
    c.statusInfo = /cannot copy note because it's missing/
