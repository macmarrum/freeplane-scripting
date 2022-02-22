// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
import org.freeplane.api.Node as FPN

/**
 * Merge several nodes, moving all their children to the first node (from
 * parent's perspective) of the bunch being merged; delete the other nodes.
 * Compare text, details, note, icons -- if different, ask for
 * a confirmation. Also when any of the other nodes contains any attributes.
 */

Collection<FPN> selecteds = c.selecteds.collect()
if (selecteds.size() < 2) {
    c.statusInfo = "merge: only ${selecteds.size()} node selected; expcted at least 2"
    return
}
// exit if not siblings
def isConfirmationNeeded = false
FPN zeroth = selecteds[0]
String commonParentId = zeroth.parent.id
for (FPN n in selecteds.drop(1)) {
    if (n.parent.id != commonParentId) {
        c.statusInfo = "merge: all nodes being merged must have the same parent"
        return
    }
    isConfirmationNeeded ?= (n.text != zeroth.text || n.detailsText != zeroth.detailsText ||
            n.noteText != zeroth.noteText || !n.attributes.empty || n.icons.icons != zeroth.icons.icons)
}
if (isConfirmationNeeded) {
    def msg = 'The nodes being merged have different text or details or note or attributes or icons\nContinue merging anyway?'
    def answer = ui.showConfirmDialog(zeroth.delegate, msg, 'Confirm merge', 0)
    if (answer != 0)
        return
}
// sort by the position of each sibling
def positionToNode = new TreeMap<Integer, FPN>()
selecteds.each { FPN n ->
    def position = n.parent.getChildPosition(n)
    positionToNode.put(position, n)
}
// use the first node as the target (this node will remain, the other will be removed)
FPN target = positionToNode.firstEntry().value
// do the merging
positionToNode.values().drop(1).each { n ->
    // move children to the target before deleting the node
    n.children.each { FPN child -> child.moveTo(target) }
    n.delete()
}
