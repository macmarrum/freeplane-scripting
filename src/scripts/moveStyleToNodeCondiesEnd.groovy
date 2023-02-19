// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Move"})
/*
 * Appends the current style to Node Conditional Styles
 */


import org.freeplane.api.Node as FPN

for (FPN node in c.selecteds) {
    def styleName = node.style.name
    if (styleName === null)
        continue
    node.conditionalStyles.add(true, null, styleName, false)
}
// No need to use toBeSelected: if node has an explicitly assigned style, it is covered by the above loop; if not, ResetStyleAction has no effect on the node
menuUtils.executeMenuItems(['ResetStyleAction',])
