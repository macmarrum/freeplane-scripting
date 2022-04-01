// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
import ConfluenceStorage
import org.freeplane.core.util.TextUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

def node = ScriptUtils.node()
def c = ScriptUtils.c()
if (node.style.name in ConfluenceStorage.style) { // ['cStorageMarkupRoot', 'cStorageMarkupMaker'])
    String markup = ConfluenceStorage.makeMarkup(node)
    TextUtils.copyToClipboard(markup)
    def mmFile = node.mindMap.file
    def xmlFileBasename = mmFile.name.replaceFirst(/\.mm$/, '.cStorage')
    def xmlFile = new File(mmFile.parent, xmlFileBasename)
    try {
        xmlFile.withWriter('UTF-8') {
            it << '<!-- vim: ft=xml \n-->\n'
            it << markup
        }
        "gvim $xmlFile.path".execute()
    } catch (RuntimeException e) {
        c.statusInfo = e.message
    }
} else {
    c.statusInfo = "cannot copy ConfluenceStorage Markup because the node style is not in ${ConfluenceStorage.style*.value}"
}
