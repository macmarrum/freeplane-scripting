// @ExecutionModes({ON_SINGLE_NODE})
def toBeSelected = new HashSet<>()
c.selecteds.each { toBeSelected.add(it.next) }
c.select(toBeSelected)
