// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
import org.freeplane.api.Node as FPN

/**
 * The opposite of merge. Split self so that each child has its own clone
 */

Collection<FPN> selecteds = c.selecteds.collect()

selecteds.each { FPN toBeSplit ->
    def parent = toBeSplit.parent
    def position = parent.getChildPosition(toBeSplit)
    toBeSplit.children.eachWithIndex { child, i ->
        if (i > 0) {
            def clone = parent.appendAsCloneWithoutSubtree(toBeSplit)
            clone.left = toBeSplit.left
            clone.moveTo(parent, ++position)
            child.moveTo(clone)
        }
    }
}

c.select(selecteds)
