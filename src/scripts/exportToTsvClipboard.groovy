// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Export"})


import io.github.macmarrum.freeplane.Export
import org.freeplane.core.util.TextUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

def text = Export.toCsvString(ScriptUtils.node(), Export.TAB)
TextUtils.copyToClipboard(text)
