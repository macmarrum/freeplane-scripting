// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})

def showAncestors = true
def showDescendants = false
def nodesToShow = new HashSet()

c.selecteds.each { self ->
    nodesToShow.add(self)
    nodesToShow.addAll(self.connectorsIn.collect { it.source })
    nodesToShow.addAll(self.connectorsOut.collect { it.target })
}
map.filter(showAncestors, showDescendants, { nodesToShow.contains(it) })
