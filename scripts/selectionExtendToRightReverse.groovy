// @ExecutionModes({ON_SINGLE_NODE})
// remove bottom children from selection
def toBeSelected = c.selecteds.findAll { it.children.size() > 0 && it.children.any { it in c.selecteds } }
//toBeSelected.removeAll { it.children.all { it !in c.selecteds } || it.children.size() == 0 }
c.select(toBeSelected)
