// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Move"})
/*
 * Appends the current style to Node Conditional Styles
 */

import org.freeplane.api.Node as FPN
import org.freeplane.features.map.MapModel
import org.freeplane.features.map.NodeModel
import org.freeplane.features.styles.ConditionalStyleModel
import org.freeplane.features.styles.IStyle
import org.freeplane.features.styles.LogicalStyleController
import org.freeplane.features.styles.mindmapmode.MLogicalStyleController

// from org.freeplane.features.styles.mindmapmode.ManageNodeConditionalStylesAction.getConditionalStyleModel
static ConditionalStyleModel getConditionalStyleModel(NodeModel node) {
//    final Controller controller = Controller.getCurrentController()
//    final NodeModel node = controller.getSelection().getSelected()
    ConditionalStyleModel conditionalStyleModel = (ConditionalStyleModel) node.getExtension(ConditionalStyleModel.class)
    if (conditionalStyleModel == null) {
        conditionalStyleModel = new ConditionalStyleModel()
        node.addExtension(conditionalStyleModel)
    }
    return conditionalStyleModel
}

//ArrayList<FPN> toBeSelected_nodesWithStyleAddedToCondies = new ArrayList<>()
for (FPN node in c.selecteds) {
    IStyle iStyle = node.style.style
    if (iStyle === null)
        continue
    NodeModel nodeModel = node.delegate
    MapModel map = node.mindMap.delegate
    ConditionalStyleModel condiStyleModel = getConditionalStyleModel(nodeModel)
    ((MLogicalStyleController) LogicalStyleController.getController()).addConditionalStyle(map, condiStyleModel, true, null, iStyle, false)
//    toBeSelected_nodesWithStyleAddedToCondies.add(node)
}
//c.select(toBeSelected_nodesWithStyleAddedToCondies)
// No need to use toBeSelected: if node has an explicitly assigned style, it is covered by the above loop; if not, ResetStyleAction has no effect on the node
menuUtils.executeMenuItems(['ResetStyleAction',])
