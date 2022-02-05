// @ExecutionModes({ON_SELECTED_NODE="/menu_bar/Mac2"})

// Based on org.freeplane.features.styles.mindmapmode.styleeditorpanel.NodeShapeControlGroup.NodeShapeChangeListener.applyValue
import org.freeplane.features.map.NodeModel
import org.freeplane.features.mode.Controller
import org.freeplane.features.nodestyle.NodeGeometryModel
import org.freeplane.features.nodestyle.NodeStyleController
import org.freeplane.features.nodestyle.mindmapmode.MNodeStyleController

MNodeStyleController styleController = (MNodeStyleController) Controller.getCurrentModeController().getExtension(NodeStyleController.class)
NodeModel nodeModel = node.delegate
styleController.setShapeConfiguration(nodeModel, NodeGeometryModel.NULL_SHAPE)
