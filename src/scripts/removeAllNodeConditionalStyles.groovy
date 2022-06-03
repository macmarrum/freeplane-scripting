// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Remove"})
//import org.freeplane.features.map.NodeModel

import org.freeplane.api.Node
import org.freeplane.features.styles.ConditionalStyleModel
//import org.freeplane.features.styles.LogicalStyleController

c.selecteds.each { Node node ->
    String styleName = node.style.name
    node.delegate.removeExtension(ConditionalStyleModel.class)
    node.style.name = styleName
}
//NodeModel nodeModel = node.delegate
//ConditionalStyleModel conditionalStyleModel = nodeModel.getExtension(ConditionalStyleModel.class)
//int i = conditionalStyleModel.styles.size() - 1
//while (i >= 0) {
//    LogicalStyleController.controller.removeConditionalStyle(conditionalStyleModel, i)
//    i--
//}
//node.style.name = node.style.name
