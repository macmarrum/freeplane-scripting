// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
import org.freeplane.api.Node as FPN

/**
 * The opposite of merge. Split parent so that each child has its own clone
 */

Collection<FPN> selecteds = c.selecteds.collect()

selecteds.each { FPN toBeSplit ->
    def parent = toBeSplit.parent
    def position = parent.getChildPosition(toBeSplit)
    toBeSplit.children.eachWithIndex { child, i ->
        if (i > 0) {
            def clone = parent.appendAsCloneWithoutSubtree(toBeSplit)
            clone.moveTo(parent, ++position)
            child.moveTo(clone)
        }
    }
}

c.select(selecteds)

/*
def parent_to_children = new LinkedHashMap<FPN, ArrayList<FPN>>()

selecteds.each { FPN selected ->
    if (selected.parent && selected.parent.parent) {
        if (parent_to_children[selected.parent] == null)
            parent_to_children[selected.parent] = new ArrayList<FPN>()
        parent_to_children[selected.parent] << selected
    }
}

parent_to_children.each { e ->
    def parent = e.key
    def parentPosition = parent.parent.getChildPosition(parent)
    e.value.eachWithIndex { selected, i ->
        if (i > 0) {
            def parentClone = parent.parent.appendAsCloneWithoutSubtree(parent)
            parentClone.moveTo(parent.parent, ++parentPosition)
            selected.moveTo(parentClone)
        }
    }
}

c.select(selecteds)
*/