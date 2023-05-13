// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})

import org.freeplane.api.Node as FN
import org.freeplane.core.util.FreeplaneVersion
import org.freeplane.features.map.MapModel
import org.freeplane.features.styles.MapStyleModel
import org.freeplane.plugin.script.proxy.ScriptUtils

// <configuration>
def shouldConsiderConditionalStyles = false
def shouldShowAncestors = false
def shouldShowDescendants = false
def shouldSelectFilteredNodes = false
// </configuration>

def STYLE_FILTER_CHOOSER_NODE_TEXT = '__styleFilterChooserNode-860a4be8-182e-4274-adf1-0c58550d100b__'
FN styleFilterChooserNode
def n = ScriptUtils.node()
def c = ScriptUtils.c()
if (n.root || n.parent.text != STYLE_FILTER_CHOOSER_NODE_TEXT) {
    styleFilterChooserNode = n.children.find { it.text == STYLE_FILTER_CHOOSER_NODE_TEXT }
    if (styleFilterChooserNode) {
        styleFilterChooserNode.delete()
    } else {
        def styleToCountDirect = [:]
        def styleToCountAll = [:]
        n.mindMap.root.findAll().each {
            def styleName = it.style.name
            if (styleName)
                styleToCountDirect[styleName] = styleToCountDirect.getOrDefault(styleName, 0) + 1
            if (shouldConsiderConditionalStyles)
                it.style.allActiveStyles.each { s -> styleToCountAll[s] = styleToCountAll.getOrDefault(s, 0) + 1 }
        }

        styleFilterChooserNode = n.createChild(STYLE_FILTER_CHOOSER_NODE_TEXT)
        styleFilterChooserNode.style.maxNodeWidth = 0
        styleFilterChooserNode.cloud.colorCode = '#f0f0f050'
        if (c.freeplaneVersion >= FreeplaneVersion.getVersion('1.11.1'))
            styleFilterChooserNode.childNodesLayout = 'TOPTOBOTTOM_BOTHSIDES_CENTERED'

        MapModel map = n.delegate.map
        def mapStyleModel = MapStyleModel.getExtension(map)
        def userStyleParentNode = mapStyleModel.getStyleNodeGroup(mapStyleModel.styleMap, mapStyleModel.STYLES_USER_DEFINED)
        userStyleParentNode.children.each {
            FN styleNode = styleFilterChooserNode.createChild()
            styleNode.style.name = it.text
            styleNode.format = 'NO_FORMAT'
            styleNode.text = it.text
            def directStyleAssignmentCount = styleToCountDirect.getOrDefault(it.text, 0)
            def detailsText = directStyleAssignmentCount.toString()
            if (shouldConsiderConditionalStyles) {
                def allStyleAssignmentCount = styleToCountAll.getOrDefault(it.text, 0)
                def conditionalStyleAssignmentCount = allStyleAssignmentCount - directStyleAssignmentCount
                detailsText += " + ${conditionalStyleAssignmentCount}"
            }
            styleNode.details = detailsText
        }
    }
} else {
    def styleName = n.style.name
    styleFilterChooserNode = n.parent
    styleFilterChooserNode.delete()
    n.mindMap.filter(shouldShowAncestors, shouldShowDescendants, {
        if (shouldConsiderConditionalStyles)
            it.hasStyle(styleName)
        else
            it.style.name == styleName
    })
    if (shouldSelectFilteredNodes)
        menuUtils.executeMenuItems(['SelectFilteredNodesAction'])
}
