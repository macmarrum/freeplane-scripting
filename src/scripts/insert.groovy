// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
//TODO: check the setting copyStylesToNewChildNodes and apply it
def toBeSelected = new ArrayList()
c.selecteds.each { toBeSelected.add(it.createChild()) }
c.select(toBeSelected)
