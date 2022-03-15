// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Paste"})

import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.features.map.clipboard.MapClipboardController
import org.freeplane.features.map.mindmapmode.clipboard.MMapClipboardController
import org.freeplane.plugin.script.ScriptingMenuUtils
import org.freeplane.plugin.script.proxy.NodeProxy

final clipboardController = MapClipboardController.controller as MMapClipboardController
def transferable = clipboardController.clipboardContents
def initialListOfChildren
def newlyPastedChildren
def toBeSelected = new LinkedList<Node>()
Controller c = ScriptingMenuUtils.c()
c.selecteds.each { Node sel ->
    initialListOfChildren = sel.children.findAll { it.visible }
    def target = ((NodeProxy) sel).delegate
    clipboardController.paste(transferable, target, false, target.newChildLeft)
    sel.folded = false
    newlyPastedChildren = sel.children.findAll { it.visible && !initialListOfChildren.contains(it) }
    toBeSelected.addAll(newlyPastedChildren)
}
c.select(toBeSelected)
