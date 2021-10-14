// @ExecutionModes({ON_SINGLE_NODE})
// remove top parents from selection
def toBeSelected = c.selecteds.minus(map.root)
toBeSelected.removeAll { it.parent !in c.selecteds }
c.select(toBeSelected)
