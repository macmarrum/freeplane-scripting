// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
/*
 * Appends the current style to Node Conditional Styles
 */
import org.freeplane.features.map.MapModel
import org.freeplane.features.map.NodeModel
import org.freeplane.features.mode.Controller
import org.freeplane.features.mode.ModeController
import org.freeplane.features.styles.ConditionalStyleModel
import org.freeplane.features.styles.IStyle
import org.freeplane.features.styles.LogicalStyleController
import org.freeplane.features.styles.mindmapmode.MLogicalStyleController

// from ManageNodeConditionalStylesAction.java
static ConditionalStyleModel getConditionalStyleModel() {
    final Controller controller = Controller.getCurrentController();
    final NodeModel node = controller.getSelection().getSelected();
    ConditionalStyleModel conditionalStyleModel = (ConditionalStyleModel) node.getExtension(ConditionalStyleModel.class);
    if(conditionalStyleModel == null){
        conditionalStyleModel = new ConditionalStyleModel();
        node.addExtension(conditionalStyleModel);
    }
    return conditionalStyleModel;
}

def style = node.style
if (style === null)
    return
IStyle iStyle = style.style
MapModel map = node.mindMap.delegate

final ModeController modeController = Controller.getCurrentModeController();
modeController.startTransaction();
((MLogicalStyleController) LogicalStyleController.getController()).addConditionalStyle(map, conditionalStyleModel, true, null, iStyle, false);
modeController.commit()
