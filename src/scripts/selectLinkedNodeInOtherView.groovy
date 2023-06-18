// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Select"})
// https://github.com/freeplane/freeplane/issues/316


import org.freeplane.core.util.MenuUtils
import org.freeplane.features.mode.Controller
import org.freeplane.features.url.NodeAndMapReference
import org.freeplane.plugin.script.proxy.ScriptUtils
import org.freeplane.view.swing.map.MapViewController

def scriptName = 'selectLinkedNodeInOtherView'

def c = ScriptUtils.c()
def node = ScriptUtils.node()
def targetNode = node.link.node
if (!targetNode) {
    def linkText = node.link.text?.replaceAll($/^freeplane:/%20/$, '') // remove the prefix because it messes up nodeAndMapReference
    if (linkText) {
        def nodeAndMapReference = new NodeAndMapReference(linkText)
        if (nodeAndMapReference.hasFreeplaneFileExtension() && nodeAndMapReference.hasNodeReference()) {
            def targetFile = new File(nodeAndMapReference.mapReference)
            if (!targetFile.absolute)
                targetFile = new File(node.mindMap.file.parent, nodeAndMapReference.mapReference)
            if (node.mindMap.file != targetFile) {
                // a different map - simply follow the link
                MenuUtils.executeMenuItems(['FollowLinkAction'])
                return
            } else {
                targetNode = node.mindMap.node(nodeAndMapReference.nodeReference)
            }
        }
    }
}
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
    c.statusInfo = "$scriptName: no link found"
}
