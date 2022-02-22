// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
import org.freeplane.api.Node as FPN

import javax.swing.JOptionPane

def regexPattern = ui.showInputDialog(c.selected.delegate, '', 'Split On Regex', JOptionPane.QUESTION_MESSAGE)
c.selecteds.each { FPN nodeToBeSplit ->
    FPN newlyCreatedChild = nodeToBeSplit  // the initial one is the original node
    nodeToBeSplit.text.split(regexPattern).each { String it ->
        newlyCreatedChild = newlyCreatedChild.createChild(it.trim())
        newlyCreatedChild.style.name = nodeToBeSplit.style.name
    }
}
