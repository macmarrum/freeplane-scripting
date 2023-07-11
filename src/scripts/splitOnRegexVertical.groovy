// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
import org.freeplane.api.Node as FPN

import javax.swing.JOptionPane

def regexPattern = ui.showInputDialog(c.selected.delegate, 'NB Special characters <([{\\^-=$!|]})?*+.>', 'Split On Regex', JOptionPane.QUESTION_MESSAGE)
if (regexPattern !== null) {
    c.selecteds.each { FPN nodeToBeSplit ->
        nodeToBeSplit.text.split(regexPattern).each { String it ->
            def newlyCreatedChild = nodeToBeSplit.createChild(it.trim())
            newlyCreatedChild.style.name = nodeToBeSplit.style.name
        }
    }
}
