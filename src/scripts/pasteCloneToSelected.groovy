// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Paste"})

import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.plugin.script.proxy.ScriptUtils

def toBeSelected = new LinkedList<Node>()
def initialChildren
Controller c = ScriptUtils.c()
c.selecteds.each{ Node sel ->
	initialChildren = sel.children.collect()
	sel.pasteAsClone()
	sel.folded = false
	toBeSelected.addAll(sel.children - initialChildren)
}
c.select(toBeSelected)
