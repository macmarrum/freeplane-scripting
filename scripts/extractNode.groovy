def kids = node.children
if (kids.size() > 0) {
	def pos = parent.getChildPosition(node)
	Collections.reverse(kids)
	kids.each{ it.moveTo(parent, pos) }
	c.select(node)
}
