// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Insert"})
import org.freeplane.api.Node as FN

/*
 * Cloud is copied alongside node's format (part of FormatCopy/FormatPaste)
 * If copyFormatToNewNodeIncludesIcons, icons are copied alongside node's format (part of FormatCopy/FormatPaste)
 */
def canCopyFormatToNewChild = config.getBooleanProperty("copyFormatToNewChild")
def toBeSelected = new LinkedList<FN>()
c.selecteds.collect().each { FN selected ->
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
