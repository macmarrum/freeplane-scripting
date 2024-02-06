// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Export"})


import io.github.macmarrum.freeplane.Export
import org.freeplane.core.util.TextUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

def settings = [
        sep: Export.TAB,
        nl: null, // don't replace NL with CR so that NL-containing values are auto-quoted and can be pasted into spreadsheet cells as multi-line values
]
def text = Export.toCsvString(ScriptUtils.node(), settings)
TextUtils.copyToClipboard(text)
