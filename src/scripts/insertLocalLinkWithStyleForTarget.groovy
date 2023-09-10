// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Insert"})
// https://github.com/freeplane/freeplane/issues/1393


import org.freeplane.api.ConditionalStyleRO
import org.freeplane.api.Node
import org.freeplane.core.util.MenuUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

// change the following settings to match your needs
def styleName = 'Local-Link Target'
def shouldClearStyleForUnlinkedNodes = true
// end of settings

def nodes = ScriptUtils.c().selecteds.collect()
MenuUtils.executeMenuItems(['AddLocalLinkAction'])
nodes[-1].conditionalStyles.add(true, null, styleName, false)

if (shouldClearStyleForUnlinkedNodes) {
    def getMatchingConditionalStyles = { Node n ->
        n.conditionalStyles.findAll { ConditionalStyleRO cs -> cs.active && cs.always && cs.styleName == styleName && !cs.last }
    }
    def root = ScriptUtils.node().mindMap.root
    List<Node> targetNodes = root.find {getMatchingConditionalStyles(it) ? true : false  }
    if (targetNodes) {
        Collection<Node> sourceNodes = root.find { it.link.node ? true : false }.collect(new HashSet<Node>()) { it.link.node }
        targetNodes.each { Node targetNode ->
            if (targetNode !in sourceNodes) {
                getMatchingConditionalStyles(targetNode).each { it.remove() }
            }
        }
    }
}
