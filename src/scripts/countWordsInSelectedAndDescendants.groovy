// @ExecutionModes({ON_SINGLE_NODE})
import org.freeplane.api.Node

def uniqueNodes = new HashSet<Node>()
c.selecteds.each { Node it ->
    uniqueNodes.addAll(it.findAll())
}
int wordCount
int charCount
int nodeCount
uniqueNodes.each {
    def text = it.transformedText
    wordCount += text.split().size()
    charCount += text.size()
    nodeCount++
}
def message = """\
${wordCount} words
${charCount} characters
in ${nodeCount} node(s), including descendants
"""
ui.informationMessage(ui.frame, message, "Word Count")
