// @ExecutionModes({ON_SINGLE_NODE})
def toBeSelected = new ArrayList()
c.selecteds.each { toBeSelected.add(it.createChild()) }
c.select(toBeSelected)
