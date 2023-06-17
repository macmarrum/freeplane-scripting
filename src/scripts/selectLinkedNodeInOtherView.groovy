// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Select"})
// https://github.com/freeplane/freeplane/issues/316


import org.freeplane.features.mode.Controller
import org.freeplane.plugin.script.proxy.ScriptUtils
import org.freeplane.view.swing.map.MapViewController

def scriptName = 'selectLinkedNodeInOtherView'

def c = ScriptUtils.c()
def node = ScriptUtils.node()
def targetNode = node.link.node
if (targetNode) {
    // based on org.freeplane.plugin.script.proxy.ControllerProxy#getMapViewManager
    def mvc = Controller.currentController.mapViewManager as MapViewController
    def thisMapModel = node.mindMap.delegate
    def thisMapView = mvc.mapView
    def otherMapView = mvc.mapViewVector.find { it !== thisMapView && it.model === thisMapModel }
    if (otherMapView) {
        otherMapView.select()
        def nodeModel = targetNode.delegate
        // based on org.freeplane.plugin.script.proxy.ControllerProxy#select(org.freeplane.api.Node)
        Controller.getCurrentModeController().getMapController().displayNode(nodeModel)
        Controller.getCurrentController().getSelection().selectAsTheOnlyOneSelected(nodeModel)
    } else {
        c.statusInfo = "$scriptName: no other view found"
    }
} else {
    c.statusInfo = "$scriptName: no local link found"
}
