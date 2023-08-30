// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})


import org.freeplane.api.MindMap
import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.freeplane.core.util.MenuUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter

def node = ScriptUtils.node()
def c = ScriptUtils.c()

static File askForFile() {
    final fileChooser = new JFileChooser()
    fileChooser.dialogTitle = 'Select old mind map to compare'
    fileChooser.multiSelectionEnabled = false
    fileChooser.fileFilter = new FileNameExtensionFilter('Mind map', 'mm')
    fileChooser.currentDirectory = ScriptUtils.node().mindMap.file.parentFile
    final returnVal = fileChooser.showOpenDialog(UITools.currentRootComponent)
    if (returnVal != JFileChooser.APPROVE_OPTION) {
        return
    }
    return fileChooser.getSelectedFile()
}

/**
 * Compare two mind maps. Mark differences in newMindMap:
 * new?
 * deleted?
 * moved?
 * changed core?
 * changed details?
 * changed note?
 * Not implemented:
 * changed attributes?
 * changed icons?
 * changed style?
 */
static void compare(MindMap newMindMap, MindMap oldMindMap) {
    println(":: compare(${newMindMap.name}, ${oldMindMap.name})")
    if (newMindMap.root.id != oldMindMap.root.id) {
        UITools.showMessage('Mind maps have different root nodes', JOptionPane.ERROR_MESSAGE)
    } else {
        compareNodeRecursively(newMindMap.root, oldMindMap)
        def c = ScriptUtils.c()
        oldMindMap.root.findAll().each { Node it ->
            if (!newMindMap.node(it.id)) {
                def parent = newMindMap.node(it.parent.id)
                def n = parent.appendChild(it)
                n.left = it.left
                n.moveTo(parent, it.parent.getChildPosition(it))
                // only add style if parent wasn't deleted too
                def pcs = parent.conditionalStyles.collect()
                if (!pcs || !pcs.find { it.always && it.styleName == 'compare-mindmap:deleted' && it.active && !it.last }) {
                    n.conditionalStyles.insert(0, true, null, 'compare-mindmap:deleted', false)
                    c.select(n)
                    MenuUtils.executeMenuItems(['AddConnectorAction'])
                }
            }
        }
    }
}

static void compareNodeRecursively(Node node, MindMap oldMindMap) {
    println(":: compareNodeRecursively(${node.id} | ${node.text})")
    def c = ScriptUtils.c()
    def oldNode = oldMindMap.node(node.id)
    c.select(node)
    if (oldNode == null) {
        // no such node in oldMindMap
        node.conditionalStyles.insert(0, true, null, 'compare-mindmap:new', false)
        MenuUtils.executeMenuItems(['AddConnectorAction'])
    } else {
        if (node.text != oldNode.text) {
            node.conditionalStyles.insert(0, true, null, 'compare-mindmap:changed-text', false)
            MenuUtils.executeMenuItems(['AddConnectorAction'])
        }
        if (node.detailsText != oldNode.detailsText) {
            node.conditionalStyles.insert(0, true, null, 'compare-mindmap:changed-details', false)
            MenuUtils.executeMenuItems(['AddConnectorAction'])
        }
        if (node.noteText != oldNode.noteText) {
            node.conditionalStyles.insert(0, true, null, 'compare-mindmap:changed-note', false)
            MenuUtils.executeMenuItems(['AddConnectorAction'])
        }
        if (!node.root) {
            if (node.parent.id != oldNode.parent.id) {
                // parent changed
                node.conditionalStyles.insert(0, true, null, 'compare-mindmap:moved-parent', false)
                MenuUtils.executeMenuItems(['AddConnectorAction'])
            } else if (node.parent.getChildPosition(node) != oldNode.parent.getChildPosition(oldNode)) {
                // position changed
                node.conditionalStyles.insert(0, true, null, 'compare-mindmap:moved-position', false)
                MenuUtils.executeMenuItems(['AddConnectorAction'])
            }
        }
        node.children.each { Node nChild ->
            compareNodeRecursively(nChild, oldMindMap)
        }
    }
}


def oldFile = askForFile()
if (oldFile) {
    def mindMap = c.mapLoader(node.mindMap.file).unsetMapLocation().withView().mindMap
    def oldMindMap = c.mapLoader(oldFile).mindMap
    compare(mindMap, oldMindMap)
}
