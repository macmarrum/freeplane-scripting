// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})

import org.freeplane.api.MindMap

import javax.swing.JOptionPane

final mindMapName = 'GTD'
final inboxStyleName = 'Inbox'

MindMap gtdMindmap = c.openMindMaps.find { it.name == mindMapName }
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
        sourceMindmapPath = makeUri(gtdMindmap.file.parentFile.relativePath(node.mindMap.file))
    else
        sourceMindmapPath = node.mindMap.file.path
    newNode.link.text = "${sourceMindmapPath}#${node.id}"
} else {
    ui.showMessage("No such mindmap is open: $mindMapName", JOptionPane.ERROR_MESSAGE)
}

/**
 * File#toURI() converts fsPaths to absolute paths
 * This method works around it to allow relative fsPaths
 * @param fsPath relative or absolute
 * @return URI
 */
static URI makeUri(String fsPath) {
    return makeUri(new File(fsPath))
}

static URI makeUri(File file) {
    if (file.absolute) {
        return file.toURI()
    } else {
        def prefixPath = file.absolutePath[0..<-file.path.size()]
        def prefixUriStr = new File(prefixPath).toURI().toString()
        def absoluteUriStr = file.toURI().toString()
        def relativeUriStr = absoluteUriStr[prefixUriStr.size()..-1]
        return relativeUriStr.toURI()
    }
}
