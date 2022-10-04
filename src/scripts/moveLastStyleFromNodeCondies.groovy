// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Move"})
/*
 * Removes the last style from Node Conditional Styles and sets it as the current style
 */

import org.freeplane.api.Node as FPN
import org.freeplane.features.map.NodeModel
import org.freeplane.features.styles.ConditionalStyleModel
import org.freeplane.features.styles.LogicalStyleController
import org.freeplane.features.styles.mindmapmode.MLogicalStyleController

def controller = LogicalStyleController.controller as MLogicalStyleController
c.selecteds.each {FPN selected ->
    NodeModel selectedModel = selected.delegate
    ConditionalStyleModel selectedConditionalStyleModel = selectedModel.getExtension(ConditionalStyleModel.class)
    if (selectedConditionalStyleModel && selectedConditionalStyleModel.styles.size() > 0) {
        def item = controller.removeConditionalStyle(selectedConditionalStyleModel, selectedConditionalStyleModel.styles.size() - 1)
        selected.style.name = item.style.toString()
    }
}
