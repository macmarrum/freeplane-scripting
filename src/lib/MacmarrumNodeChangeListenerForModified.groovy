// https://github.com/freeplane/freeplane/issues/727


import org.freeplane.api.Node
import org.freeplane.api.NodeChangeListener
import org.freeplane.api.NodeChanged
import org.freeplane.core.util.TextUtils

import static org.freeplane.api.NodeChanged.ChangedElement

class MacmarrumNodeChangeListenerForModified implements NodeChangeListener {
    public static canReact = true
    private static dateTimeFormat = new TextUtils().defaultDateTimeFormat
    private static ATTR_NAME = 'Modified'

    static void updateAttrModified(Node n) {
        n[ATTR_NAME] = null
        n[ATTR_NAME] = dateTimeFormat.format(new Date())
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
