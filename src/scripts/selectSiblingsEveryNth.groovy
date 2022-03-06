// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Select"})

import org.freeplane.api.Node as FPN

import javax.swing.*

def parentToNodePosition = new HashMap<FPN, Integer>()
for (FPN it in c.selecteds) {
    if (parentToNodePosition.containsKey(it.parent)) {
        def msg = "More than 1 selected node per parent:\n${it.parent.shortText}"
        ui.showMessage(msg, JOptionPane.ERROR_MESSAGE)
        return
    } else {
        parentToNodePosition.put(it.parent, it.parent.getChildPosition(it))
    }
}

String input = ui.showInputDialog(c.selected.delegate, '', 'Select Every Nth', JOptionPane.QUESTION_MESSAGE)
if (input !== null) {
    Integer everyNth
    try {
        everyNth = Integer.valueOf(input)
    } catch (NumberFormatException ignored) {
        ui.showMessage("Not a number: $input", JOptionPane.ERROR_MESSAGE)
        return
    }
    def toBeSelected = new LinkedList<FPN>()
    parentToNodePosition.each { startingFrom -> append(toBeSelected, everyNth, startingFrom) }
    c.select(toBeSelected)
}

static void append(List<FPN> toBeSelected, Integer everyNth, Map.Entry<FPN, Integer> startingFrom) {
    final parent = startingFrom.key
    final selectedPosition = startingFrom.value
    final childrenCount = parent.children.size()
    (selectedPosition..<childrenCount).step(everyNth).each { i -> toBeSelected << parent.children[i] }
}