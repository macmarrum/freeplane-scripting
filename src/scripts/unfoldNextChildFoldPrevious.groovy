// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})

import org.freeplane.plugin.script.proxy.ScriptUtils


def node = ScriptUtils.node()
def children = node.children
def childrenSize = children.size()
if (childrenSize > 0) {
    if (node.folded) {
        node.folded = false
        def nonLeafChildren = children.findAll { !it.leaf }
        nonLeafChildren.each { if (!it.folded) it.folded = true }
    }
    def lastUnfoldedChild = children.reverse().find { !it.leaf && !it.folded }
    def lastUnfoldedChildPosition = lastUnfoldedChild ? node.getChildPosition(lastUnfoldedChild) : -1
    def nextChildPosition = lastUnfoldedChildPosition + 1
    while (nextChildPosition < childrenSize && children[nextChildPosition].leaf) {
        nextChildPosition++
    }
    if (nextChildPosition < childrenSize)
        children[nextChildPosition].folded = false
    lastUnfoldedChild?.folded = true
}
