// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
def toBeSelected = new HashSet()
c.selecteds.each { self ->
    toBeSelected.addAll(self.parent.children.findAll { it.visible })
}
c.select(toBeSelected)