// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Select"})
// https://github.com/freeplane/freeplane/issues/316


import org.freeplane.core.ui.components.UITools
import org.freeplane.core.util.MenuUtils
import org.freeplane.features.map.MapModel
import org.freeplane.features.map.NodeModel
import org.freeplane.features.mode.Controller
import org.freeplane.features.url.NodeAndMapReference
import org.freeplane.plugin.script.proxy.ScriptUtils
import org.freeplane.view.swing.map.MapViewController

import javax.swing.JOptionPane
import java.nio.charset.StandardCharsets

def c = ScriptUtils.c()
def node = ScriptUtils.node()
def targetNode = node.link.node // resolves #ID and #at
def linkText = node.link.text?.replaceAll($/^freeplane:/%20/$, '') // remove the prefix because it messes up nodeAndMapReference
if (!targetNode) { // not the case of same-map target node or a full node URI is used as the link
    // work out what the target node is
    if (linkText && node.link.uri.scheme !in ['menuitem', 'file', 'https', 'http']) {
        def nodeAndMapReference = new NodeAndMapReference(linkText)
        if (nodeAndMapReference.hasFreeplaneFileExtension() && nodeAndMapReference.hasNodeReference()) {
            def thisFile = node.mindMap.file
            def mapReferenceDecoded = URLDecoder.decode(nodeAndMapReference.mapReference, StandardCharsets.UTF_8.name())
            def targetFile = new File(mapReferenceDecoded)
            if (!targetFile.absolute) {
                if (!thisFile) {
                    MenuUtils.executeMenuItems(['SaveAsAction'])
                    thisFile = node.mindMap.file
                    if (!thisFile) // Save-As action was aborted
                        return
                }
                targetFile = new File(thisFile.parent, mapReferenceDecoded)
            }
            if (thisFile?.canonicalFile <=> targetFile.canonicalFile) {
                // a different map - simply follow the link
                c.statusInfo = "${this.class.simpleName}:  $linkText (following it) "
                println("thisFile:   $thisFile.canonicalFile")
                println("targetFile: $targetFile.canonicalFile")
                MenuUtils.executeMenuItems(['FollowLinkAction'])
                return
            } else {
                targetNode = node.mindMap.node(nodeAndMapReference.nodeReference)
            }
        }
    } else { // synchronize views by switching to the same node in another view
        targetNode = node // comment out this line to disable same-node view sync
        linkText = "#${node.id} (the same node in another view) "
    }
}
if (targetNode) { // same-map target node
    def targetNodeModel = targetNode.delegate as NodeModel
    // based on org.freeplane.plugin.script.proxy.ControllerProxy#getMapViewManager
    def mvc = Controller.currentController.mapViewManager as MapViewController
    def thisMapModel = node.mindMap.delegate as MapModel
    def thisMapView = mvc.mapView
    def otherMapView = mvc.mapViewVector.find {
        try {
            it !== thisMapView && it.map === thisMapModel
        } catch (MissingPropertyException ignore) { // before 1.11.8-pre01
            it !== thisMapView && it.model === thisMapModel
        }
    }
    if (!otherMapView) { // open another map view if not exists
        Controller.getCurrentModeController().getMapController().createMapView(thisMapModel)
        otherMapView = mvc.mapViewVector.find {
            try {
                it !== thisMapView && it.map === thisMapModel
            } catch (MissingPropertyException ignore) { // before 1.11.8-pre01
                it !== thisMapView && it.model === thisMapModel
            }
        }
        UITools.showMessage('A new map view has been created', JOptionPane.INFORMATION_MESSAGE)
    }
    otherMapView.select()
    // based on org.freeplane.plugin.script.proxy.ControllerProxy#select(org.freeplane.api.Node)
    Controller.getCurrentModeController().getMapController().displayNode(targetNodeModel)
    Controller.getCurrentController().getSelection().selectAsTheOnlyOneSelected(targetNodeModel)
    c.statusInfo = "${this.class.simpleName}:  $linkText "
} else {
    c.statusInfo = "${this.class.simpleName}:  no link found! "
}

