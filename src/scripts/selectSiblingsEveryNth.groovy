// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Select"})

import org.freeplane.api.Node as FPN
import org.freeplane.core.ui.components.UITools
import org.freeplane.features.map.NodeModel
import org.freeplane.plugin.script.proxy.ScriptUtils

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

final c = ScriptUtils.c()
NodeModel nodeModel = ScriptUtils.node().delegate
String input = UITools.showInputDialog(nodeModel, '', 'Select Every Nth', JOptionPane.QUESTION_MESSAGE)
if (input !== null) {
    int everyNth
    try {
        everyNth = input as int
    } catch (NumberFormatException ignored) {
        UITools.showMessage("Not a number: '$input'", JOptionPane.ERROR_MESSAGE)
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