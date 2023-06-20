// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
// https://github.com/freeplane/freeplane/issues/1161


import org.freeplane.api.Node
import org.freeplane.features.filter.FilterController
import org.freeplane.plugin.script.proxy.ScriptUtils

def filterController = FilterController.currentFilterController
def showAncestors = filterController.showAncestors.selected
if (!showAncestors) {
    def c = ScriptUtils.c()
    def node = ScriptUtils.node()
    def showDescendants = filterController.showDescendants.selected
    // for each selected node, find its next invisible ancestor
    def expandedFilterHash = c.selecteds.collect(new HashSet<Node>()) { it.pathToRoot.drop(1).reverse().find { !it.visible } }
    // add all nodes that are already visible
    expandedFilterHash.addAll(node.mindMap.root.find { !it.root && it.visible })
    node.mindMap.filter(showAncestors, showDescendants, { it in expandedFilterHash })
}
