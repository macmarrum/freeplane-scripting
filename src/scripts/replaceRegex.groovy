// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
import org.freeplane.api.Node as FPN

import javax.swing.JOptionPane

def regexPattern = ui.showInputDialog(c.selected.delegate, 'NB Special characters <([{\\^-=$!|]})?*+.>', 'Replace Regex', JOptionPane.QUESTION_MESSAGE)
if (regexPattern !== null) {
    def lst = regexPattern.tokenize(regexPattern[0])
    c.selecteds.each { FPN n ->
        if (lst.size() == 3 && lst[2] == 'g')
            n.text = n.text.replaceAll(lst[0], lst[1])
        else
            n.text = n.text.replaceFirst(lst[0], lst[1])
    }
}
