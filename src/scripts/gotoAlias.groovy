// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
import org.freeplane.api.Node
import org.freeplane.features.explorer.NodeAlias
import org.freeplane.features.explorer.NodeAliases
import org.freeplane.features.mode.Controller
import org.freeplane.plugin.script.proxy.ScriptUtils

import javax.swing.JOptionPane

final c = ScriptUtils.c()
final Controller controller = Controller.getCurrentController()
final Collection<NodeAlias> nodeAliases = NodeAliases.of(controller.map).aliases()
final String[] aliases = nodeAliases.collect { it.value }.sort().toArray()
String input = JOptionPane.showInputDialog(null, null, "Go to alias",
        JOptionPane.QUESTION_MESSAGE, null, aliases, null)
if (input == null || input == '') {
    c.statusInfo = 'input is null or blank'
    return
} else {
    Node dest = c.findAll().find { it.alias == input }
    if (dest)
        c.select(dest)
    else
        c.statusInfo = "$input not found"
}
