// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
def toBeSelected = new HashSet<>()
toBeSelected.addAll(c.selecteds)
c.selecteds.each { toBeSelected.addAll(it.children) }
c.select(toBeSelected)
