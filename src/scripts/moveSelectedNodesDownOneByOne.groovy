// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Move"})
import org.freeplane.api.Node
import org.freeplane.core.util.MenuUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

final nodeDownAction = ['NodeDownAction']
final c = ScriptUtils.c()
final selecteds = c.selecteds.collect()
selecteds.each { Node it ->
    c.select(it)
    MenuUtils.executeMenuItems(nodeDownAction)
}
c.select(selecteds)
