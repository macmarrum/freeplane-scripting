// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})


import org.freeplane.api.Node
import org.freeplane.core.util.HtmlUtils
import org.freeplane.features.icon.factory.IconStoreFactory
import org.freeplane.features.map.MapModel
import org.freeplane.features.map.NodeModel
import org.freeplane.features.styles.*
import org.freeplane.features.styles.mindmapmode.MLogicalStyleController

import static org.freeplane.features.styles.MapStyleModel.STYLES_USER_DEFINED

def materializedSymlinkIStyle = StyleFactory.create('MaterializedSymlink')
def snowflake = 'emoji-2744'
def formulaPrefix = ' '

MapModel mapModel = node.mindMap.delegate

// set up the style
def materializedSymlinkStyleNode = getOrCreateUserDefStyle(mapModel, materializedSymlinkIStyle)
if (!materializedSymlinkStyleNode.icons.any { it.name == snowflake }) {
    def namedIcon = IconStoreFactory.ICON_STORE.getMindIcon(snowflake)
    materializedSymlinkStyleNode.addIcon(namedIcon)
}

def logicalStyleController = LogicalStyleController.controller as MLogicalStyleController
String lnk
String text
c.selecteds.each { Node it ->
    lnk = it.link.text
    if (lnk) {
        def condiStyleModel = getOrCreateConditionalStyleModelOf(it.delegate)
        logicalStyleController.addConditionalStyle(mapModel, condiStyleModel, true, null, materializedSymlinkIStyle, false)
        it['pasteAsSymlinkUri'] = formulaPrefix + lnk
        if (it.text.startsWith('=')) {
            // save the original formula
            it['pasteAsSymlinkText'] = formulaPrefix + it.text
            // materialize content
            text = it.transformedText
            it.text = text
        }
        def detailsText = it.detailsText
        if (detailsText) {
            def plainDetailsText = HtmlUtils.htmlToPlain(detailsText)
            if (plainDetailsText.startsWith('=')) {
                // save the original formula
                it['pasteAsSymlinkDetails'] = formulaPrefix + plainDetailsText
                // materialize content
                text = it.details
                it.details = text
            }
        }
        def noteText = it.noteText
        if (noteText) {
            def plainNoteText = HtmlUtils.htmlToPlain(noteText)
            if (plainNoteText.startsWith('=')) {
                // save the original formula
                it['pasteAsSymlinkNote'] = formulaPrefix + plainNoteText
                // materialize content
                text = it.note
                it.note = text
            }
        }
    }
}

static NodeModel getOrCreateUserDefStyle(MapModel mapModel, IStyle iStyle) {
    def mapStyleModel = MapStyleModel.getExtension(mapModel)
    def styleNode = mapStyleModel.getStyleNode(iStyle)
    if (!styleNode) {
        styleNode = new NodeModel(mapModel)
        styleNode.setUserObject(iStyle)
        def userStyleParentNode = mapStyleModel.getStyleNodeGroup(mapStyleModel.styleMap, STYLES_USER_DEFINED)
//        def userStyleParentNode = mapStyleModel.styleMap.root.children.find { (it.userObject as StyleTranslatedObject).object == STYLES_USER_DEFINED }
//        (Controller.currentModeController.mapController as MMapController).insertNode(styleNode, userStyleParentNode) // event triggered
        userStyleParentNode.insert(styleNode, userStyleParentNode.childCount) // no event triggered
        mapStyleModel.addStyleNode(styleNode)
    }
    return styleNode
}

static ConditionalStyleModel getOrCreateConditionalStyleModelOf(NodeModel node) {
    def conditionalStyleModel = node.getExtension(ConditionalStyleModel.class) as ConditionalStyleModel
    if (conditionalStyleModel == null) {
        conditionalStyleModel = new ConditionalStyleModel()
        node.addExtension(conditionalStyleModel)
    }
    return conditionalStyleModel
}
