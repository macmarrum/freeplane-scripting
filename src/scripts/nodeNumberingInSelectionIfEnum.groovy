// @ExecutionModes({ON_SELECTED_NODE="/menu_bar/Mac1"})
import org.freeplane.api.NodeRO

import java.util.regex.Pattern

final Boolean canUseDotAfterNumber = node['#dot'] ? node['#dot'].num0 : false
final String attrib = '#'

// clean up
def pat
node.findAll().drop(1).each {
    if (it[attrib]) {
//        if (canUseDotAfterNumber) {
            // FP replaces '1.' with '1' (number recognition) when editing a node,
            // therefore looking for a number _optionally_ followed by a dot
            pat = Pattern.compile("^${it[attrib]}\\.? ?")
//        } else {
//            pat = Pattern.compile("^${it[attrib]} ?")
//        }
        it.text = "${it.text.replaceAll(pat, '')}"
        it[attrib] = null
    }
}
// find enum nodes to process and group them by parent
def enumMap = new HashMap<NodeRO, List<NodeRO>>()
node.findAll().drop(1).findAll { it.hasStyle('=Enum#') }.each {
    if (!enumMap[it.parent]) enumMap[it.parent] = new ArrayList<NodeRO>()
    enumMap[it.parent].add(it)
}
// insert (replace) the numbering in text
int i
enumMap.values().each { enumChildren ->
    i = 0
    enumChildren.each {
        it[attrib] = ++i
        if (canUseDotAfterNumber)
            it.text = it.text ? "${i}. ${it.text}" : "${i}."
        else
            it.text = it.text ? "${i} ${it.text}" : i
    }
}
