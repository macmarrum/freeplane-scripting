def kids = node.children
if (kids.size() > 0) {
	def pos = node.parent.getChildPosition(node)
	Collections.reverse(kids)
	kids.each{ it.moveTo(node.parent, pos) }
	node.delete()
	c.select(kids[0])
}
