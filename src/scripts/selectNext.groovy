// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
def toBeSelected = new HashSet<>()
c.selecteds.each { toBeSelected.add(it.next) }
c.select(toBeSelected)
