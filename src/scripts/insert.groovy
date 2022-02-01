// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})

def canCopyFormatToNewChild = config.getBooleanProperty("copyFormatToNewChild")
def canCopyFormatToNewNodeIncludesIcons = config.getBooleanProperty("copyFormatToNewNodeIncludesIcons")
def toBeSelected = new ArrayList()
c.selecteds.each { selected ->
    def newNode = selected.createChild()
    if (canCopyFormatToNewChild) {
        newNode.style.name = selected.style.name
        if (canCopyFormatToNewNodeIncludesIcons)
            selected.icons.each { icon -> newNode.icons.add(icon) }
    }
    toBeSelected.add(newNode)
}
c.select(toBeSelected)
