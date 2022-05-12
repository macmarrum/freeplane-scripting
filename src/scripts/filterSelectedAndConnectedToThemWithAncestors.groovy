// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
import org.freeplane.features.filter.FilterController

def filterController = FilterController.currentFilterController
def showAncestors = true
def showDescendants = filterController.showDescendants.selected
def nodesToShow = new HashSet()

c.selecteds.each { selected ->
    nodesToShow.add(selected)
    selected.connectorsIn.each { if (it.source) nodesToShow.add(it) }
    selected.connectorsOut.each { if (it.target) nodesToShow.add(it) }
}
node.mindMap.filter(showAncestors, showDescendants, { nodesToShow.contains(it) })
