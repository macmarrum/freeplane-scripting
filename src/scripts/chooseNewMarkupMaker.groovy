// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})

import org.freeplane.plugin.script.proxy.ScriptUtils

import javax.swing.*

final c = ScriptUtils.c()
final action = [
        'list': ConfluenceStorage::createList,
        'table': ConfluenceStorage::createTable,
        'code': ConfluenceStorage::createCode,
        'link': ConfluenceStorage::createLink,
        'div-expand+code': ConfluenceStorage::createDivExpandCode,
        'format': ConfluenceStorage::createFormat,
        'parent': ConfluenceStorage::createParent,
        'csv': ConfluenceStorage::createCsv,
]
String input = JOptionPane.showInputDialog(null, null, "Go to alias",
        JOptionPane.QUESTION_MESSAGE, null, action.keySet().toArray(), null)
if (input == null || input == '') {
    c.statusInfo = 'input is null or blank'
    return
} else {
    def method = action.getOrDefault(input, null)
    if (method)
        method(node)
    else
        c.statusInfo = "no method by key $input"
}
