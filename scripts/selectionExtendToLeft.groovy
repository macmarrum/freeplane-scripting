// @ExecutionModes({ON_SINGLE_NODE})
def toBeSelected = new HashSet<>()
toBeSelected.addAll(c.selecteds)
c.selecteds.each { toBeSelected.add(it.parent) }
c.select(toBeSelected)