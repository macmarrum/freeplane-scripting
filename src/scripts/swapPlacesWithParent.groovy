// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
def kids
def self
def par
int myPos
int parPos
def toBeSelected = new ArrayList()
def selecteds = c.selecteds.collect()  // a clone
for (n in selecteds) {
	// no swapping with root
	if (n.parent.isRoot()) continue

	// no swaping with a hidden parent, e.g. a SummaryNode
	// NB. a summary node is made of:
	//     1. FirstGroupNode (hidden) indicating the start point
	//     2. SummaryNode (hidden) indicating the end point
	//		 3. SummaryNode's child, with the actual text
	//  Freeplane displays an accolade symbol between 1 and 2, with 3 at its confluence point
	if (!n.parent.visible) continue

	// at the end, this will be selected
	toBeSelected.add(n.parent)

	// remember the objects, because they will move and their relative names won't work
	self = n
	par = n.parent

	if (self.children.size() > 0) kids = self.children
	myPos = par.getChildPosition(self)
	parPos = par.parent.getChildPosition(par)

	// create temp node in my position
	if (kids) temp = par.createChild(myPos)

	// move children to temp
	if (kids) kids.each{ it.moveTo(temp) }

	// move to grandparent
	self.moveTo(par.parent, parPos)

	// move siblings to myself
	par.children.each{ it.moveTo(self) }

	// move par to my initial position
	par.moveTo(self, myPos)

	// move kids to par
	if (kids) kids.each{ it.moveTo(par) }

	// remove temp
	if (kids) temp.delete()
}
c.select(toBeSelected)
