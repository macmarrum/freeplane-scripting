// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
import javax.swing.JOptionPane

final mindMapName = 'GTD'
final inboxStyleName = 'Inbox'

def gtdMindmap = c.openMindMaps.find { it.name == mindMapName }
if (gtdMindmap) {
    def title = 'Capture to GTD Inbox'
    def text = ''
    def capturedText = ui.showInputDialog(node.delegate, text, title, JOptionPane.QUESTION_MESSAGE)
    if (!capturedText)
        return
//    def customDateFormatForThisScriptOnly = 'yyyy-mm-dd,E'
//    def defaultDateFormatAsSetInPreferences = config.getProperty('date_format')
    def defaultDateTimeFormatAsSetInPreferences = config.getProperty('datetime_format')
    def inboxNode = gtdMindmap.root.findAll().find { it.style.name == inboxStyleName }
    def newNode = inboxNode.createChild(capturedText)
    newNode.details = format(new Date(), defaultDateTimeFormatAsSetInPreferences).toString()
    String sourceMindmapPath
    if (gtdMindmap == node.mindMap)
        sourceMindmapPath = ''
    else if (config.getProperty('links') == 'relative')
        sourceMindmapPath = gtdMindmap.file.parentFile.relativePath(node.mindMap.file)
    else
        sourceMindmapPath = node.mindMap.file.path
    newNode.link.text = "${sourceMindmapPath}#${node.id}"
} else {
    ui.showMessage("No such mindmap is open: $mindMapName", JOptionPane.ERROR_MESSAGE)
}
