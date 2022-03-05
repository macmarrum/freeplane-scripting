// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Select"})
def toBeSelected = new HashSet<org.freeplane.api.NodeRO>()
boolean isLeft = c.selected.isLeft()
c.selecteds.each { self ->
    toBeSelected.addAll(self.parent.children.findAll { it.visible && it.left == isLeft})
}
toBeSelected.removeAll(c.selecteds)
c.select(toBeSelected)
