// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
import org.freeplane.api.Node as FPN
if (node.isRoot()) return

def siblings
def positions
c.selecteds.each { FPN self ->
// TODO: think about better information when siblings are selected
//       currently the first one selected wins silently
    siblings = self.parent.children.minus(self).findAll {it.left == self.left && !it.delegate.hiddenSummary }
    if (siblings.size() > 0) {
        positions = siblings.collect { self.parent.getChildPosition(it) }
        siblings.eachWithIndex { it, idx -> it.moveTo(self, positions[idx]) }
    }
}