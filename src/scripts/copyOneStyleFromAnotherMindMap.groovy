// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
// alternative location: "//main_menu/format//menu_manageStyles"})
// based on CopyMapStylesAction.java

import org.freeplane.api.MindMap
import org.freeplane.core.ui.components.UITools
import org.freeplane.features.mode.Controller
import org.freeplane.features.mode.ModeController
import org.freeplane.features.url.mindmapmode.MFileManager

import javax.swing.*

// get the name of the style
def title = 'Copy a single Style'
def text = 'Enter the name of the Style to be copied\nNext you will choose the source mind map'
def styleName = UITools.showInputDialog(node.delegate, text, title, JOptionPane.QUESTION_MESSAGE)
if (!styleName)
    return false

// get the source map
final Controller controller = Controller.getCurrentController()
final ModeController modeController = controller.getModeController()
final MFileManager fileManager = MFileManager.getController(modeController)
final JFileChooser fileChooser = fileManager.getMindMapFileChooser()
fileChooser.setMultiSelectionEnabled(false)
final int returnVal = fileChooser.showOpenDialog(controller.getMapViewManager().getMapViewComponent())
if (returnVal != JFileChooser.APPROVE_OPTION) {
    return
}
File file = fileChooser.getSelectedFile()
if (!file.exists()) {
    return
}
// load the source mind map
MindMap source = c.mapLoader(file).mindMap

// do the copying
node.mindMap.copyStyleFrom(source, styleName)
