// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
import org.freeplane.api.Node
import org.freeplane.features.filter.FilterController

def filterController = FilterController.currentFilterController
def showAncestors = true
def showDescendants = filterController.showDescendants.selected
node.mindMap.filter(showAncestors, showDescendants, { c.selecteds.any { Node selected -> it.transformedText == selected.transformedText } })
