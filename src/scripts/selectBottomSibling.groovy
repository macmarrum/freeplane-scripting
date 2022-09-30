// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Select"})
import org.freeplane.api.Node
import org.freeplane.plugin.script.proxy.ScriptUtils

def node = ScriptUtils.node()
def c = ScriptUtils.c()
def isAtRoot = node.parent == c.viewRoot
def sideAtRoot = node.sideAtRoot
c.select(node.parent.children.reverse().find { Node it -> it.visible && isAtRoot ? it.sideAtRoot == sideAtRoot : true })
