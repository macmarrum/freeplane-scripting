// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Jump"})

import org.freeplane.core.util.MenuUtils
import org.freeplane.plugin.script.proxy.ScriptUtils


// put the name of your custom style, if different
def jumpInStyleName = 'JumpIn'

def node = ScriptUtils.node()
def styleAlreadyAdded = node.conditionalStyles.find { it.active && it.always && it.styleName == jumpInStyleName && !it.last }
if (!styleAlreadyAdded)
    node.conditionalStyles.insert(0, true, null, jumpInStyleName, false)
MenuUtils.executeMenuItems(['JumpInAction'])
