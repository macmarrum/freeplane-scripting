// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Jump"})


import org.freeplane.api.ConditionalStyle
import org.freeplane.core.util.FreeplaneVersion
import org.freeplane.core.util.MenuUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

// put the name of your custom style - must be the same as in jumpInWithStyle.groovy
def jumpInStyleName = 'JumpIn'

static def clearJumpInStyleAndBackgroundColor(node, jumpInStyleName) {
    def preJumpInBackgroundColorCode = node['preJumpInBackgroundColor']
    if (preJumpInBackgroundColorCode) {
        node.mindMap.backgroundColorCode = preJumpInBackgroundColorCode == 'null' ? null : preJumpInBackgroundColorCode
        node['preJumpInBackgroundColor'] = null
    }
    node.conditionalStyles.findAll {
        it.active && it.always && it.styleName == jumpInStyleName && !it.last
    }.each { ConditionalStyle it ->
        it.remove()
    }
}

def node = ScriptUtils.node()
def c = ScriptUtils.c()
def viewRoot = c.viewRoot
if (viewRoot != node.mindMap.root) {
    clearJumpInStyleAndBackgroundColor(viewRoot, jumpInStyleName)
    MenuUtils.executeMenuItems(['JumpOutAction'])
//    if (FreeplaneVersion.version < new FreeplaneVersion(1, 11, 1)) {
//        // force view refresh
//        node.style.name = node.style.name
//    }
} else {
    clearJumpInStyleAndBackgroundColor(node, jumpInStyleName)
}
