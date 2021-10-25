// @ExecutionModes({ON_SINGLE_NODE})
def myPosition
def toBeSelected = new HashSet()
def toBeSelectedFallback = new HashSet()
c.selecteds.collect().each { self ->
    if (self.children.size() > 0) {
        // remember my children, to be selected at the end
        toBeSelected.addAll(self.children.findAll { it.visible })
        // move each of my children to my parent, at my position
        myPosition = self.parent.getChildPosition(self)
        self.children.reverse().each { it.moveTo(self.parent, myPosition) }
        // say good bye and cross over to the other side
        self.delete()
    } else {  // do a simple delete
        // remember my parent, to be selected at the end (as a fallback option, if no children from anyone)
        toBeSelectedFallback.add(self.parent)
        self.delete()
    }
}
c.select(toBeSelected.size() > 0 ? toBeSelected : toBeSelectedFallback)
