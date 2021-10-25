// @ExecutionModes({ON_SINGLE_NODE})
def toBeSelected = new HashSet<>()
c.selecteds.each { toBeSelected.add(it.previous) }
c.select(toBeSelected)
