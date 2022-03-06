// @ExecutionModes({ON_SELECTED_NODE="/menu_bar/Mac1/Numbering"})
import org.freeplane.api.Node as FPN

import java.util.regex.Pattern

final Boolean canUseDotAfterNumber = node['#dot'] ? node['#dot'].num0 : false
final String attrib = '#'
final String enumStyle = '=Enum#'

Collection<FPN> selecteds = c.selecteds.collect()
// group selected by parent
def parentToPositionsOfSelected = new HashMap<FPN, Set<Integer>>()
selecteds.each { FPN it ->
    if (!parentToPositionsOfSelected[it.parent])
        parentToPositionsOfSelected[it.parent] = new TreeSet<Integer>() // sorts selected by their position
    parentToPositionsOfSelected[it.parent].add(it.parent.getChildPosition(it))
}
// insert (replace) the numbering in text
GString numbering
FPN parent
FPN child
Pattern pat
int i
parentToPositionsOfSelected.each { entry ->
    parent = entry.key
    i = 0
    entry.value.each { position ->
        child = parent.children[position]
        if (!child.hasStyle(enumStyle))
             child.style.name = enumStyle
        final textWithoutNumbering = child.text.replaceAll(/^${child[attrib]}[.]? ?/, '')
        child[attrib] = ++i
        numbering = "${i}${canUseDotAfterNumber ? '.' : ''}"
        child.text = child.text && textWithoutNumbering ? "${numbering} ${textWithoutNumbering}" : numbering
    }
}
