// @ExecutionModes({ON_SINGLE_NODE})
def toBeSelected = new HashSet()
c.selecteds.each { self ->
    toBeSelected.addAll(self.parent.children.findAll { it.visible })
}
c.select(toBeSelected)