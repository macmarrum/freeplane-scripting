// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Insert"})
import org.freeplane.api.Node as FPN

/*
 * If copyFormatToNewChild, format is copied from the parent
 * else if copyFormatToNewSibling, format is copied from the selected node (child-to-be)
 * Cloud is copied alongside the format
 * No format is copied if both options are off
 * If copyFormatToNewNodeIncludesIcons, icons are copied alongside the format
 */
boolean canCopyFormatToNewChild = config.getBooleanProperty("copyFormatToNewChild")
boolean canCopyFormatToNewSibling = config.getBooleanProperty("copyFormatToNewSibling")
boolean canCopyFormatToNewNodeIncludesIcons = config.getBooleanProperty("copyFormatToNewNodeIncludesIcons")
FPN parent
FPN newParent
FPN source
def selecteds = c.selecteds.collect()
def positions = selecteds.collect { FPN it -> it.parent.getChildPosition(it) }
Integer position
def toBeSelected = new HashSet<FPN>()

selecteds.eachWithIndex { FPN selected, idx ->
    parent = selected.parent
    position = positions[idx]
    newParent = parent.createChild(position)
    newParent.left = selected.left
    selected.moveTo(newParent)
    if (canCopyFormatToNewChild || canCopyFormatToNewSibling) {
        source = canCopyFormatToNewChild ? parent : canCopyFormatToNewSibling ? selected : null
        newParent.style.name = source.style.name
        newParent.cloud.color = source.cloud.color
        newParent.cloud.shape = source.cloud.shape
        if (canCopyFormatToNewNodeIncludesIcons)
            source.icons.each { icon -> newParent.icons.add(icon) }
    }
    toBeSelected.add(newParent)
}
c.select(toBeSelected)
