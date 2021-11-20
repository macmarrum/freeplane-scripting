// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
// deselect the lowest level among selected
final Boolean canCountHidden = false
def maxLevel = c.selecteds.collect { it.getNodeLevel(canCountHidden) }.max()
def toBeSelected  = c.selecteds.findAll { it.getNodeLevel(canCountHidden) < maxLevel }
c.select(toBeSelected)
