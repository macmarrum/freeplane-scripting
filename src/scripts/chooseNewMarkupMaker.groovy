// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})

import org.freeplane.plugin.script.proxy.ScriptUtils
import io.github.macmarrum.freeplane.ConfluenceStorage

import javax.swing.*

final c = ScriptUtils.c()
final title = 'Insert markup maker'
final action = [
        'list': ConfluenceStorage::createList,
        'link': ConfluenceStorage::createLink,
        'table': ConfluenceStorage::createTable,
        'code': ConfluenceStorage::createCode,
        'csv': ConfluenceStorage::createCsv,
        'div-expand+code': ConfluenceStorage::createDivExpandCode,
        'format': ConfluenceStorage::createFormat,
        'parent': ConfluenceStorage::createParent,
]
String input = JOptionPane.showInputDialog(null, null, title,
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
