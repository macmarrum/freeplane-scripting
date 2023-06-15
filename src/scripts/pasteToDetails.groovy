// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Paste"})

import org.freeplane.plugin.script.proxy.ScriptUtils

import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable

def t = Toolkit.defaultToolkit.systemClipboard.getContents(null)
def text = getString(t)
if (text)
    ScriptUtils.node().detailsText = text

private static String getString(Transferable t) {
    if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        try {
            return t.getTransferData(DataFlavor.stringFlavor).toString()
        } catch (ignored) {
        }
    }
    ScriptUtils.c().statusInfo = 'pasteToDetails: error getting clipboard contents'
    return null
}
