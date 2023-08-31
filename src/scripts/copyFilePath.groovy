// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Copy"})

import org.freeplane.core.util.TextUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

TextUtils.copyToClipboard(ScriptUtils.node().mindMap.file.path)
