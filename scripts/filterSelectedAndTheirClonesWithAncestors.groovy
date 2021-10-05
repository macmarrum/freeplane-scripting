// @ExecutionModes({ON_SINGLE_NODE})
import org.freeplane.api.NodeRO

def showAncestors = true
def showDescendants = false
def nodesToShow = new HashSet()

c.selecteds.each { self ->
    nodesToShow.add(self)
    nodesToShow.addAll(self.nodesSharingContent)
}
map.filter(showAncestors, showDescendants, { nodesToShow.contains(it) })
