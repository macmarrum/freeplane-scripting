// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/InvertSelection"})

def selecteds = c.selecteds.collect()
def allVisibleNodes = node.mindMap.root.find { it.visible }
def toBeSelected = allVisibleNodes - selecteds
c.select(toBeSelected)
