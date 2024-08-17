// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Select"})

import org.freeplane.api.Node as FPN


def canCountHidden = false

def selecteds = c.selecteds.collect()

def levelToSelecteds = new HashMap<Integer, List<FPN>>()
c.selecteds.each { FPN n ->
    Integer level = n.getNodeLevel(canCountHidden)
    if (!levelToSelecteds[level])
        levelToSelecteds[level] = []
    levelToSelecteds[level] << n
}
def selectedLevels = levelToSelecteds.keySet()

def levelToToBeSelected = new HashMap<Integer, List<FPN>>()
node.mindMap.root.find { it.visible && !selecteds.contains(it) }.each { FPN n ->
    Integer level = n.getNodeLevel(canCountHidden)
    if (level in selectedLevels) {
        if (!levelToToBeSelected[level])
            levelToToBeSelected[level] = []
        levelToToBeSelected[level] << n
    }
}

def toBeSelected = levelToToBeSelected.values().flatten()
c.select(toBeSelected)
