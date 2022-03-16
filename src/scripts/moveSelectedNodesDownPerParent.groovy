// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Move"})
import org.freeplane.api.Node
import org.freeplane.core.util.MenuUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

final nodeDownAction = ['NodeDownAction']
final c = ScriptUtils.c()
final selecteds = c.selecteds.collect()
def parentToNodes = new HashMap<Node, List<Node>>()
selecteds.each { Node it ->
    if (!parentToNodes[it.parent])
        parentToNodes[it.parent] = new LinkedList<Node>()
    parentToNodes[it.parent] << it
}
// move nodes in batches, per each parent
parentToNodes.each { entry ->
    c.select(entry.value)
    MenuUtils.executeMenuItems(nodeDownAction)
}
c.select(selecteds)
