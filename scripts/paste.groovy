// @ExecutionModes({ON_SINGLE_NODE})
/*
 * Alternative "paste" functionality
 *
 * Pastes the copied content into each selected node
 * and selects the pasted nodes
 *
 * Extends https://www.freeplane.org/wiki/index.php/Scripts_collection#Paste_clipboard
 */
import org.freeplane.features.map.mindmapmode.clipboard.MMapClipboardController

def clipboardController = MMapClipboardController.controller
def transferable = clipboardController.clipboardContents
def initialListOfChildren
def newlyPastedChildren
def toBeSelected = new ArrayList()
c.selecteds.each { self ->
    initialListOfChildren = self.children.findAll { it.visible }
    def target = self.delegate
    clipboardController.paste(transferable, target, false, target.newChildLeft)
    self.folded = false
    newlyPastedChildren = self.children.findAll { it.visible && !initialListOfChildren.contains(it) }
    toBeSelected.addAll(newlyPastedChildren)
}
c.select(toBeSelected)
