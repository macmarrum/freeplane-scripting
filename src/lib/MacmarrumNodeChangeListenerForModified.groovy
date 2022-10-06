// https://github.com/freeplane/freeplane/issues/727
import org.freeplane.api.Node
import org.freeplane.api.NodeChangeListener
import org.freeplane.api.NodeChanged
import org.freeplane.plugin.script.FreeplaneScriptBaseClass.ConfigProperties

import java.time.LocalDateTime

import static org.freeplane.api.NodeChanged.ChangedElement

class MacmarrumNodeChangeListenerForModified implements NodeChangeListener {
    public static canReact = true
    private static dateTimeFormat = new ConfigProperties().getProperty('datetime_format')
    private static ATTR_NAME = 'Modified'

    static void updateAttrModified(Node n) {
        n[ATTR_NAME] = LocalDateTime.now().format(dateTimeFormat)
    }

    void nodeChanged(NodeChanged event) {
        /* enum ChangedElement {TEXT, DETAILS, NOTE, ICON, ATTRIBUTE, FORMULA_RESULT, UNKNOWN} */
        if (!canReact)
            return
        canReact = false
        switch (event.changedElement) {
            case [ChangedElement.TEXT, ChangedElement.NOTE, ChangedElement.ICON]:
                updateAttrModified(event.node)
        }
        canReact = true
    }
}
