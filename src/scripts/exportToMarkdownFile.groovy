// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Export"})


import io.github.macmarrum.freeplane.Export
import org.freeplane.plugin.script.proxy.ScriptUtils

def node = ScriptUtils.node()

def suggestedFile = new File(node.mindMap.file.path.replaceAll(/\.mm$/, '.md'))
def file = Export.askForFile(suggestedFile)
println(":: ${new Date().format('yyyy-MM-dd HH:mm:ss')} exportToCsvFile `${file.path}'")
Export.toMarkdownFile(file, node)
