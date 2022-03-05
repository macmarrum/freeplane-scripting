// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Select"})
def toBeSelected = new HashSet<>()
c.selecteds.each { toBeSelected.add(it.next) }
c.select(toBeSelected)
