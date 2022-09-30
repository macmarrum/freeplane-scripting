// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Move"})
/*
 * Removes the last style from Node Conditional Styles and sets it as the current style
 */

import org.freeplane.api.Node as FPN
import org.freeplane.features.map.NodeModel
import org.freeplane.features.styles.ConditionalStyleModel
import org.freeplane.features.styles.LogicalStyleController

c.selecteds.each {FPN selected ->
    NodeModel selectedModel = selected.delegate
    ConditionalStyleModel selectedConditionalStyleModel = selectedModel.getExtension(ConditionalStyleModel.class)
    if (selectedConditionalStyleModel && selectedConditionalStyleModel.styles.size() > 0) {
        def item = LogicalStyleController.controller.removeConditionalStyle(selectedConditionalStyleModel, 0)
        selected.style.name = item.style.toString()
    }
}
