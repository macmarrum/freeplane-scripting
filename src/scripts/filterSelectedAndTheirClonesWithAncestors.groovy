// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
import org.freeplane.api.Node

def showAncestors = true
def showDescendants = false
def nodesToShow = new HashSet<Node>()

c.selecteds.each { Node sel ->
    nodesToShow.add(sel)
    nodesToShow.addAll(sel.nodesSharingContent)
}
node.mindMap.filter(showAncestors, showDescendants, { nodesToShow.contains(it) })
