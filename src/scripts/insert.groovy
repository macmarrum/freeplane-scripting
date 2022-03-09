// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Insert"})
import org.freeplane.api.Node as FPN

/*
 * Cloud is copied alongside the format (part of FormatCopy/FormatPaste)
 * If copyFormatToNewNodeIncludesIcons, icons are copied alongside the format (part of FormatCopy/FormatPaste)
 */
def canCopyFormatToNewChild = config.getBooleanProperty("copyFormatToNewChild")
def toBeSelected = new LinkedList<FPN>()
c.selecteds.collect().each { FPN selected ->
    def newNode = selected.createChild()
    if (canCopyFormatToNewChild) {
        c.select(selected)
        menuUtils.executeMenuItems(['FormatCopy'])
        c.select(newNode)
        menuUtils.executeMenuItems(['FormatPaste'])
    }
    toBeSelected.add(newNode)
}
c.select(toBeSelected)
