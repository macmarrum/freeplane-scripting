// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
def myPosition
def myNewParent
def toBeSelected = new HashSet()
def selecteds = c.selecteds.collect()
def positions = selecteds.collect { it.parent.getChildPosition(it) }

selecteds.eachWithIndex { self, idx ->
    myPosition = positions[idx]
    myNewParent = self.parent.createChild(myPosition)
    myNewParent.setLeft(self.isLeft())
    self.moveTo(myNewParent)
    toBeSelected.add(myNewParent)
}
c.select(toBeSelected)