// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})

import org.freeplane.api.Node
import org.freeplane.api.Node as FPN
import javax.swing.JOptionPane

FPN oneOfThem = c.selected
def isSameText = !c.selecteds.any { it.text != oneOfThem.text }
if (isSameText) {
    edit(oneOfThem)
} else {
    def decision = ui.showConfirmDialog(node.delegate, 'Selected nodes have different core text\nBulk-edit anyway?', 'Error editing node core in-line', JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE)
    if (decision == 0)
        edit(oneOfThem)
}

private void edit(FPN oneOfThem) {
    def message = "Edit ${c.selecteds.size()} node(s) in-line"
    def initialValue = oneOfThem.text
    def newText = ui.showInputDialog(oneOfThem.delegate, message, initialValue)
    c.selecteds.each { it.text = newText }
}
