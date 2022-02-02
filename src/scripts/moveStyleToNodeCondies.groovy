// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
/*
 * Appends the current style to Node Conditional Styles
 */


import org.freeplane.features.map.MapModel
import org.freeplane.features.map.NodeModel
import org.freeplane.features.styles.ConditionalStyleModel
import org.freeplane.features.styles.IStyle
import org.freeplane.features.styles.LogicalStyleController
import org.freeplane.features.styles.mindmapmode.MLogicalStyleController

// from ManageNodeConditionalStylesAction.java
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

for (node in c.selecteds) {
    IStyle iStyle = node.style.style
    if (iStyle === null)
        continue
    NodeModel nodeModel = node.delegate
    MapModel map = node.mindMap.delegate
    ConditionalStyleModel condiStyleModel = getConditionalStyleModel(nodeModel)
    ((MLogicalStyleController) LogicalStyleController.getController()).addConditionalStyle(map, condiStyleModel, true, null, iStyle, false)
}
menuUtils.executeMenuItems(['ResetStyleAction',])
