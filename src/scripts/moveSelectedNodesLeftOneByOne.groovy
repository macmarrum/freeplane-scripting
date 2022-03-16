// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Move"})
import org.freeplane.core.util.MenuUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

final changeNodeLevelLeftsAction = ['ChangeNodeLevelLeftsAction']
final c = ScriptUtils.c()
final selecteds = c.selecteds.collect()
selecteds.each {
    c.select(it)
    MenuUtils.executeMenuItems(changeNodeLevelLeftsAction)
}
c.select(selecteds)
