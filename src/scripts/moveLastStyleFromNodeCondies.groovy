// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Move"})
/*
 * Removes the last style from Node Conditional Styles and sets it as the current style
 */

import org.freeplane.api.Node as FPN

c.selecteds.each { FPN selected ->
    def conditionalStylesSize = selected.conditionalStyles.collect().size()
    if (conditionalStylesSize > 0) {
        def item = selected.conditionalStyles.remove(conditionalStylesSize - 1)
        selected.style.name = item.styleName
    }
}
