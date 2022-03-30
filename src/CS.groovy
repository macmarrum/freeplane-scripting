import org.freeplane.api.NodeRO

class CS {
    final static String ATTRIB_NAME = 'condiStyle'
    final static String attr = ATTRIB_NAME

    def static canApply(NodeRO node, String condiStyle, Boolean condition) {
        if (condition) {
            node[ATTRIB_NAME] = condiStyle
            return true
        } else {
            if (node[ATTRIB_NAME].text == condiStyle)
                node[ATTRIB_NAME] = null
            return false
        }
    }
}
