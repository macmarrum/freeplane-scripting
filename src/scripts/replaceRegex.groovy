// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
import org.freeplane.api.Node as FPN
import org.freeplane.core.resources.ResourceController
import org.freeplane.core.ui.components.UITools
import org.freeplane.features.map.NodeModel
import org.freeplane.plugin.script.proxy.ScriptUtils

final String default_browser_command_windows_9x = "default_browser_command_windows_9x"
final resourceController = ResourceController.getResourceController()
final c = ScriptUtils.c()
final NodeModel nodeModel = c.selected.delegate
final String initialValue = resourceController.getProperty(default_browser_command_windows_9x)
String input = UITools.showInputDialog(nodeModel, 'Replace Regex\nE.g. /pattern/replacement/g\nNB Special characters <([{\\^-=$!|]})?*+.>', initialValue)
if (input !== null) {
    resourceController.setProperty(default_browser_command_windows_9x, input)
    final token = input[0]
    final tokenCount = input.count(token)
    if (tokenCount != 3) {
        c.statusInfo = "Replace Regex: token count is $tokenCount - expected 3"
        return
    }
    final lst = input.tokenize(token)
    final String pattern = lst[0]
    final boolean isGlobal = input.endsWith("${token}g")
    String replacement
    if ((isGlobal && input.endsWith("$token${token}g")) || input.endsWith("$token$token"))
        replacement = ''
    else
        replacement = lst[1]
    c.selecteds.each { FPN n ->
        if (isGlobal)
            n.text = n.text.replaceAll(pattern, replacement)
        else
            n.text = n.text.replaceFirst(pattern, replacement)
    }
}
