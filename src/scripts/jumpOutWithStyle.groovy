// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Jump"})


import org.freeplane.api.ConditionalStyle
import org.freeplane.core.util.FreeplaneVersion
import org.freeplane.core.util.MenuUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

// put the name of your custom style - must be the same as in jumpInWithStyle.groovy
def jumpInStyleName = 'JumpIn'

def node = ScriptUtils.node()
def c = ScriptUtils.c()
c.viewRoot.conditionalStyles.findAll {
    it.active && it.always && it.styleName == jumpInStyleName && !it.last
}.each { ConditionalStyle it ->
    it.remove()
}
def viewRoot = c.viewRoot
def jumpInBackgroundColorCode = viewRoot['jumpInBackgroundColor']
if (jumpInBackgroundColorCode) {
    node.mindMap.backgroundColorCode = jumpInBackgroundColorCode
    viewRoot['jumpInBackgroundColor'] = null
}
MenuUtils.executeMenuItems(['JumpOutAction'])
if (FreeplaneVersion.version < new FreeplaneVersion(1, 11, 1)) {
    // force view refresh
    node.style.name = node.style.name
}
