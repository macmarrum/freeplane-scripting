// @ExecutionModes({ON_SINGLE_NODE})
def toBeSelected = new HashSet()
c.selecteds.each { self ->
    toBeSelected.addAll(self.parent.children.findAll { it.visible })
}
toBeSelected.removeAll(c.selecteds)
c.select(toBeSelected)
