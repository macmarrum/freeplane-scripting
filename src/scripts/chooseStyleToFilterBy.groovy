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

def STYLE_FILTER_CHOOSER_NODE_TEXT = ':style-filter chooser:'
def n = ScriptUtils.node()
def c = ScriptUtils.c()

if (!n.root && n.parent.text == STYLE_FILTER_CHOOSER_NODE_TEXT) {
    def styleName = n.style.name
    def detailsText = n.details.text
    def isConditionalStyleAssignmentCount = detailsText.contains('+') && !detailsText.endsWith('+ 0')
    n.mindMap.filter(shouldShowAncestors, shouldShowDescendants, {
        if (shouldConsiderConditionalStyles && isConditionalStyleAssignmentCount)
            it.hasStyle(styleName)
        else
            it.style.name == styleName
    })
    if (shouldSelectFilteredNodes)
        menuUtils.executeMenuItems(['SelectFilteredNodesAction'])
} else {
    def styleFilterChooserNode = n.mindMap.root.children.find { it.text == STYLE_FILTER_CHOOSER_NODE_TEXT }
    if (styleFilterChooserNode) {
        c.select(styleFilterChooserNode)
        n.mindMap.filter = null
        styleFilterChooserNode.folded = false
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

        styleFilterChooserNode = n.mindMap.root.createChild(STYLE_FILTER_CHOOSER_NODE_TEXT)
        println(styleFilterChooserNode.id)
        styleFilterChooserNode.style.name = null
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
        c.select(styleFilterChooserNode)
    }
}
