// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
// https://github.com/freeplane/freeplane/issues/1405


import org.freeplane.core.util.MenuUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

def UNFOLD_ALL_ACTION = ['UnfoldAllAction']
def FOLD_ALL_ACTION = ['FoldAllAction']
def c = ScriptUtils.c()
def selectedNodes = c.selecteds.collect()
def root = c.viewRoot
if (root in selectedNodes) {
    c.select(root)
    if (root.children.findAll { !it.leaf }.every { it.isFolded() }) {
        MenuUtils.executeMenuItems(UNFOLD_ALL_ACTION)
    } else {
        MenuUtils.executeMenuItems(FOLD_ALL_ACTION)
    }
} else {
    def topNodes = selectedNodes.findAll { it.parent !in selectedNodes }
    topNodes.each { node ->
        c.select(node)
        if (node.isFolded()) {
            MenuUtils.executeMenuItems(UNFOLD_ALL_ACTION)
        } else {
            MenuUtils.executeMenuItems(FOLD_ALL_ACTION)
        }
    }
    c.select(topNodes)
}
