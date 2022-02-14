// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac3"})
import org.freeplane.api.Node as FPN
import org.freeplane.api.NodeChangeListener
import org.freeplane.api.NodeChanged
import org.freeplane.api.NodeChanged.ChangedElement
import org.freeplane.plugin.script.FreeplaneScriptBaseClass.ConfigProperties

class MacmarrumNodeChangeListener implements NodeChangeListener {
    static boolean canReact = true

    void nodeChanged(NodeChanged event) {
        /* enum ChangedElement {TEXT, DETAILS, NOTE, ICON, ATTRIBUTE, FORMULA_RESULT, UNKNOWN} */
        if (!canReact)
            return
        if (event.changedElement == ChangedElement.TEXT) {
            def config = new ConfigProperties()
            def max_shortened_text_length = config.getIntProperty("max_shortened_text_length")
            if (event.node.to.plain.size() > max_shortened_text_length) {
                event.node.setMinimized(true)
            } else {
                event.node.setMinimized(false)
            }
        }
    }
}

def listeners = node.mindMap.listeners_
String alias = 'minimize_nodes_if_text_is_longer_NCL'
List<FPN> foundlings = c.find {FPN it -> it.isGlobal && it.alias == alias }
FPN target = foundlings.size() == 1 ? foundlings[0] : null
if (!target) {
    target = node.mindMap.root.createChild(alias.replaceAll(/_/, ' '))
    target.setLeft(true)
    target.setIsGlobal(true)
    target.setAlias(alias)
    target['runMe'] = new URI('menuitem:_MinimizeNodesIfTextIsLongerNCL_on_single_node')
}
String now = format(new Date(), X.dfLong)
String triangular_flag = 'emoji-1F6A9'
if (listeners.size() > 0) {
    listeners.each { if (it.class.name == 'MacmarrumNodeChangeListener') node.mindMap.removeListener(it) }
    target['off'] = now
    target.icons.remove(triangular_flag)
} else {
    target['on'] = now
    target.icons.add(triangular_flag)
    node.mindMap.addListener(new MacmarrumNodeChangeListener())
}
