// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
import org.freeplane.api.Node as FN
import org.freeplane.plugin.script.proxy.ScriptUtils

List<FN> children
FN self
FN par
FN temp
int myPos
int parPos
def c = ScriptUtils.c()
def selecteds = c.selecteds.collect()  // a clone
def toBeSelected = new ArrayList<FN>(selecteds.size())
for (n in selecteds) {
    // no swapping with root
    if (n.parent.isRoot())
        continue

    // no swapping with a hidden parent, e.g. a SummaryNode
    // NB. a summary node is made of:
    //     1. FirstGroupNode (hidden) indicating the start point
    //     2. SummaryNode (hidden) indicating the end point
    //		 3. SummaryNode's child, with the actual text
    //  Freeplane displays an accolade symbol between 1 and 2, with 3 at its confluence point
    if (!n.parent.visible)
        continue

    // remember the objects, because they will move and their relative names won't work
    self = n
    par = n.parent

	toBeSelected.add(par) // at the end, this will be selected

    myPos = par.getChildPosition(self)
	parPos = par.parent.getChildPosition(par)

	children = self.children.size() > 0 ? self.children : null
	if (children) {
        temp = par.createChild(myPos) // create temp node at my position
        children.each {
            it.moveTo(temp)
        }
    } else {
		temp = null
    }

    self.moveTo(par.parent, parPos) // move to grandparent

    par.children.each { // move siblings to myself
        it.moveTo(self)
    }

    par.moveTo(self, myPos) // move par to my initial position

    if (children) {
        children.each {
            it.moveTo(par)
        }
        temp.delete()
    }
}
c.select(toBeSelected)