// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
def toBeSelected = new ArrayList()
def initialChildren
c.selecteds.each{ self ->
	initialChildren = self.children.collect()
	self.pasteAsClone()
	self.folded = false
	toBeSelected.addAll(self.children.minus(initialChildren))
}
c.select(toBeSelected)
