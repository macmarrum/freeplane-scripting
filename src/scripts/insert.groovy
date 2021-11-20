// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
def toBeSelected = new ArrayList()
c.selecteds.each { toBeSelected.add(it.createChild()) }
c.select(toBeSelected)
