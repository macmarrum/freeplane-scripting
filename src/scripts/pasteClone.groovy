// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Paste"})
def toBeSelected = new LinkedList()
def initialChildren
c.selecteds.each{ self ->
	initialChildren = self.children.collect()
	self.pasteAsClone()
	self.folded = false
	toBeSelected.addAll(self.children.minus(initialChildren))
}
c.select(toBeSelected)
