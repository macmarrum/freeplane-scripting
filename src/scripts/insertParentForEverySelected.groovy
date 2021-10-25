// @ExecutionModes({ON_SINGLE_NODE})
def myPosition
def myNewParent
def toBeSelected = new HashSet()
def selecteds = c.selecteds.collect()
def positions = selecteds.collect { it.parent.getChildPosition(it) }

selecteds.eachWithIndex { self, idx ->
    myPosition = positions[idx]
    myNewParent = self.parent.createChild(myPosition)
    self.moveTo(myNewParent)
    toBeSelected.add(myNewParent)
}
c.select(toBeSelected)