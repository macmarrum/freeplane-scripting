// @ExecutionModes({ON_SINGLE_NODE})
// https://www.freeplane.org/wiki/index.php/Scripts_collection#Paste_clipboard
import org.freeplane.features.map.mindmapmode.clipboard.MMapClipboardController

def clipboardController = MMapClipboardController.controller
def transferable = clipboardController.clipboardContents
c.selecteds.each{
	def target = it.delegate
	clipboardController.paste(transferable, target, false, target.newChildLeft)
	it.folded = false
}
