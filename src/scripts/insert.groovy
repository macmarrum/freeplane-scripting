// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Insert"})
import org.freeplane.api.Node as FPN

def canCopyFormatToNewChild = config.getBooleanProperty("copyFormatToNewChild")
def canCopyFormatToNewNodeIncludesIcons = config.getBooleanProperty("copyFormatToNewNodeIncludesIcons")
def toBeSelected = new LinkedList<FPN>()
c.selecteds.each { FPN selected ->
    def newNode = selected.createChild()
    if (canCopyFormatToNewChild) {
        newNode.style.name = selected.style.name
        newNode.cloud.color = selected.cloud.color
        newNode.cloud.shape = selected.cloud.shape
        if (canCopyFormatToNewNodeIncludesIcons)
            selected.icons.each { icon -> newNode.icons.add(icon) }
    }
    toBeSelected.add(newNode)
}
c.select(toBeSelected)
