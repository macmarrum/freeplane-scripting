// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
import ConfluenceStorage
import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.util.TextUtils
import org.freeplane.plugin.script.FreeplaneScriptBaseClass
import org.freeplane.plugin.script.proxy.ScriptUtils

Node node = ScriptUtils.node()
Controller c = ScriptUtils.c()
if (node.style.name in ConfluenceStorage.style) { // ['cStorageMarkupRoot', 'cStorageMarkupMaker'])
    String markup = ConfluenceStorage.makeMarkup(node)
    TextUtils.copyToClipboard(markup)
    openInEditorIfDefined(c, node, markup)
} else {
    c.statusInfo = "cannot copy ConfluenceStorage Markup because the node style is not in ${ConfluenceStorage.style*.value}"
}

/*
 * Uses default_browser_command_mac to define the editor -- must be in PATH
 */
static void openInEditorIfDefined(Controller c, Node node, String markup) {
    def config = new FreeplaneScriptBaseClass.ConfigProperties()
    def default_browser_command_mac = config.getProperty('default_browser_command_mac')
    if (default_browser_command_mac && default_browser_command_mac != 'open') {
        File mmFile = node.mindMap.file
        def xmlFileBasename = mmFile.name.replaceFirst(/\.mm$/, '.cStorage')
        def xmlFile = new File(mmFile.parent, xmlFileBasename)
        try {
            xmlFile.withWriter('UTF-8') {
                it << '<!-- vim: set ft=xml: -->\n'
                it << markup
            }
            [default_browser_command_mac, xmlFile.path].execute()
        } catch (RuntimeException e) {
            c.statusInfo = e.message
        }
    }
}
