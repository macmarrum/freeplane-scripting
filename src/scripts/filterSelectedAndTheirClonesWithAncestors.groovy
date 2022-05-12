// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
import org.freeplane.api.Node
import org.freeplane.features.filter.FilterController

def filterController = FilterController.currentFilterController
def showAncestors = true
def showDescendants = filterController.showDescendants.selected
def nodesToShow = new HashSet<Node>()

c.selecteds.each { Node sel ->
    nodesToShow.add(sel)
    nodesToShow.addAll(sel.nodesSharingContent)
}
node.mindMap.filter(showAncestors, showDescendants, { nodesToShow.contains(it) })
