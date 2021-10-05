import groovy.transform.Field
import org.freeplane.api.NodeRO

@Field final static String ATTRIB_NAME = 'condiStyle'

def static canApply(NodeRO node, String condiStyle, Boolean condition) {
    if (condition.is(true)) {
        node[ATTRIB_NAME] = condiStyle
        return true
    } else {
        if (node[ATTRIB_NAME] == condiStyle) {
            node[ATTRIB_NAME] = null
        }
        return false
    }
}