// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Select"})
final boolean countHidden = false  // FP cannot select hidden nodes, incl SummaryNode
final boolean mutateCollectionInPlace = true
def selecteds = c.selecteds.collect()  // a copy
def uniqueLevels = selecteds.collect{ it.getNodeLevel(countHidden) }.unique(mutateCollectionInPlace)
def toBeSelected
if (uniqueLevels.size() == 1) {
	// select the parents
	toBeSelected = selecteds.collect{ it.parent }.unique(mutateCollectionInPlace)
} else {
	// unselect all except for the top level
	final int minLevel = uniqueLevels.min()
	toBeSelected = selecteds.findAll{ it.getNodeLevel(countHidden) == minLevel }
}
c.select(toBeSelected)
