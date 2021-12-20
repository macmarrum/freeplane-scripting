// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
def myPosition
c.selecteds.each { self ->
	if (self.children.size() > 0) {
		myPosition = self.parent.getChildPosition(self)
		self.children.reverse().each { it.moveTo(self.parent, myPosition) }
	}
}