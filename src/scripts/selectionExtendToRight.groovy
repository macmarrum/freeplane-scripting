// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Select"})
def toBeSelected = new HashSet<>()
toBeSelected.addAll(c.selecteds)
c.selecteds.each { toBeSelected.addAll(it.children) }
c.select(toBeSelected)
