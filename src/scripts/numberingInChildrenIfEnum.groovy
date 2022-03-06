// @ExecutionModes({ON_SELECTED_NODE="/menu_bar/Mac1/Numbering"})
import org.freeplane.api.Node as FPN

import java.util.regex.Pattern

final Boolean canUseDotAfterNumber = node['#dot'] ? node['#dot'].num0 : false
final String attrib = '#'
final String enumStyle = '=Enum#'

List<FPN> operands = node.children
// run through all children and clean up
def pat
operands.each {
    if (it[attrib]) {
        // FP replaces '1.' with '1' (number recognition) when editing a node,
        // therefore looking for a number _optionally_ followed by a dot
        pat = Pattern.compile("^${it[attrib]}\\.? ?")
        it.text = "${it.text.replaceAll(pat, '')}"
        it[attrib] = null
    }
}
// find enum nodes to process and group them by parent
def parentToEnumChildren = new HashMap<FPN, List<FPN>>()
operands.each { FPN it ->
    if (it.hasStyle(enumStyle)) {
        if (!parentToEnumChildren[it.parent])
            parentToEnumChildren[it.parent] = new LinkedList<FPN>()
        parentToEnumChildren[it.parent].add(it)
    }
}
// insert (replace) the numbering in text
int i
GString numbering
parentToEnumChildren.values().each { enumChildren ->
    i = 0
    enumChildren.each { child ->
        child.style.name = enumStyle
        child[attrib] = ++i
        numbering = "${i}${canUseDotAfterNumber ? '.' : ''}"
        child.text = child.text ? "${numbering} ${child.text}" : numbering
    }
}
