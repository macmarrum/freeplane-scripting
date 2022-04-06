// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
import org.freeplane.plugin.script.proxy.ScriptUtils

ScriptUtils.c().selecteds.each { it.hideDetails = !it.hideDetails }
