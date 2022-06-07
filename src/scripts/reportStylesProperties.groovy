// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
/**
 * Creates a tab-separated report of all Styles with their properties
 * - copies it to clipboard
 * - saves it in Note of "Styles" node (child of root)
 */
import org.freeplane.core.util.ColorUtils
import org.freeplane.core.util.TextUtils
import org.freeplane.features.cloud.CloudModel
import org.freeplane.features.edge.EdgeModel
import org.freeplane.features.format.IFormattedObject
import org.freeplane.features.map.NodeModel
import org.freeplane.features.nodelocation.LocationModel
import org.freeplane.features.nodestyle.*
import org.freeplane.features.note.NoteModel
import org.freeplane.features.styles.MapStyleModel
import org.freeplane.features.text.DetailModel
import org.freeplane.features.text.TextController

import java.awt.*

static String getFormat(NodeModel nodeModel) {
    final String format = TextController.controller.getNodeFormat(nodeModel)
    if (format === null && nodeModel.userObject instanceof IFormattedObject)
        return (nodeModel.userObject as IFormattedObject).pattern
    return format
}

static def colorToString(Color color) {
    if (color === null)
        return null
    def stringColor = ColorUtils.colorToString(color)
    return ColorUtils.isNonTransparent(color) ? stringColor : "transparent $stringColor".toString()
}

def none = 'none'
def m = MapStyleModel.getExtension(node.delegate.map)
def styleProperties = m.styles.collect { it ->
    def n = m.getStyleNode(it)
    def nsm = n.getExtension(NodeStyleModel.class)
    def nodeCss = n.getExtension(NodeCss.class)
    def nodeGeometryModel = nsm?.shapeConfiguration === NodeGeometryModel.NULL_SHAPE ? null : nsm?.shapeConfiguration
    def nodeSizeModel = n.getExtension(NodeSizeModel.class)
    def locationModel = n.getExtension(LocationModel.class)
    def nodeBorderModel = n.getExtension(NodeBorderModel.class)
    def edgeModel = n.getExtension(EdgeModel.class)
    def cloudModel = n.getExtension(CloudModel.class)
    def d = [
            Style               : it,
            'Node Color:'       : none,
            Text                : colorToString(nsm?.color),
            Background          : colorToString(nsm?.backgroundColor),
            'Node Font:'        : none,
            'Font family'       : nsm?.fontFamilyName,
            'Font Size'         : nsm?.fontSize,
            'Bold'              : nsm?.isBold(),
            'Strike through'    : nsm?.isStrikedThrough(),
            'Italic'            : nsm?.isItalic(),
            'Text Alignment'    : nsm?.horizontalTextAlignment,
            'CSS'               : nodeCss != null ? nodeCss.css[0..<Math.min(nodeCss.css.size(), 20)] : null,
            'Icons:'              : none,
            'Icon size'         : n.sharedData.icons.iconSize,
            'Core text:'        : none,
            Format              : nsm?.nodeFormat,
            'Node numbering'    : nsm?.nodeNumbering,
            'Content types:'    : none,
            'For details'       : DetailModel.getDetail(n)?.contentType, // ?: TextController.CONTENT_TYPE_HTML,
            'For note'          : NoteModel.getNote(n)?.contentType, // ?: TextController.CONTENT_TYPE_HTML,
            'Node shape:'       : none,
            'Node shape'        : nodeGeometryModel?.shape, // nsm?.shape,
            'Horizontal margin' : nodeGeometryModel?.horizontalMargin, // nsm?.shapeConfiguration?.horizontalMargin,
            'Vertical margin'   : nodeGeometryModel?.verticalMargin, // nsm?.shapeConfiguration?.verticalMargin,
            'Uniform?'          : nodeGeometryModel?.uniform, // nsm?.shapeConfiguration?.uniform,
            'Min node width'    : nodeSizeModel?.minNodeWidth, //NodeSizeModel.getMinNodeWidth(n),
            'Max node width'    : nodeSizeModel?.maxNodeWidth, //NodeSizeModel.getMaxNodeWidth(n),
            'Child gap'         : locationModel?.VGap, // LocationModel.getModel(n)?.VGap, // ?: LocationModel.DEFAULT_VGAP,
            'Node border:'      : none,
            'Line width'        : nodeBorderModel?.borderWidth,
            'Use edge width'    : nodeBorderModel?.borderWidthMatchesEdgeWidth,
            'Use edge line type': nodeBorderModel?.borderDashMatchesEdgeDash,
            'Border line type'  : nodeBorderModel?.borderDash,
            'Use edge color'    : nodeBorderModel?.borderColorMatchesEdgeColor,
            'Color'             : colorToString(nodeBorderModel?.borderColor),
            'Edges:'            : none,
            'Edge width'        : edgeModel?.width,
            'Edge line type'    : edgeModel?.dash,
            'Edge style'        : edgeModel?.style,
            'Edge color'        : colorToString(edgeModel?.color),
            'Clouds:'           : none,
            'Cloud color'       : colorToString(cloudModel?.color),
            'Cloud shape'       : cloudModel?.shape, // ?: CloudShape.ARC,
    ]
    return d.collect { e -> e.value == none ? e.key : "${e.key}\t${e.value}" }.join('\n')
}
def root = node.mindMap.root
(root.children.find { it.text == 'Styles' } ?: root.createChild('Styles')).note = styleProperties.join('\n')
TextUtils.copyToClipboard(styleProperties.join('\n'))
