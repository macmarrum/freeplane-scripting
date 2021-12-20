// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
import org.freeplane.api.Node as FPN

def selecteds = c.selecteds.collect()
if (selecteds.size() < 2) {
    c.statusInfo = "merge: ${selecteds.size()} selected. Expcted at least 2"
    return
}
// sort by the position of each sibling (if not siblings, will exit later)
def positionToNode = new TreeMap<Integer, FPN>()
selecteds.each { FPN n ->
    def position = n.parent.getChildPosition(n)
    positionToNode.put(position, n)
}
// use the first node as the target (this node will remain, the other will be removed)
FPN target = positionToNode.get(0)
// make sure the nodes are siblings
def commonParentId = target.parent.id
for (n in positionToNode.values().drop(1)) {
    if (n.parent.id != commonParentId) {
        c.statusInfo = "merge: nodes being merged must have the same parent"
        return
    }
}
// do the merging
positionToNode.values().drop(1).each {n ->
    // move children to the target before deleting the node
    n.children.each { FPN child -> child.moveTo(target) }
    n.delete()
}
