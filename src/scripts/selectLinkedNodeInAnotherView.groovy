/*
 * Copyright (C) 2023-2025  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Select"})
// https://github.com/freeplane/freeplane/issues/316


import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.freeplane.core.util.FreeplaneVersion
import org.freeplane.core.util.MenuUtils
import org.freeplane.features.map.MapModel
import org.freeplane.features.map.NodeModel
import org.freeplane.features.mode.Controller
import org.freeplane.features.url.NodeAndMapReference
import org.freeplane.view.swing.map.MapViewController

import javax.swing.*
import java.nio.charset.StandardCharsets

import static org.freeplane.plugin.script.GroovyStaticImports.config

c = c as org.freeplane.api.Controller
node = node as Node
def FP_VER = FreeplaneVersion.version
def FP_1_12_9 = FreeplaneVersion.getVersion('1.12.9')

static def toggleSpotlight() {
    // org.freeplane.features.styles.SetBooleanMapViewPropertyAction.actionPerformed
    def propertyName = 'spotlight'
    def mapViewComponent = Controller.getCurrentController().getMapViewManager().getMapViewComponent()
    final Boolean value = Boolean.TRUE.equals(mapViewComponent.getClientProperty(propertyName))
    boolean newValue = !value.booleanValue()
    mapViewComponent.putClientProperty(propertyName, newValue)
}

Node targetNode = null
String linkText = null
if (config.getBooleanProperty('_select_linked_node_in_another_view', true)) {
    targetNode = node.link.node // resolves #ID and #at
    linkText = node.link.text?.replaceFirst($/^freeplane:/%20/$, '') // remove the prefix because it messes up nodeAndMapReference
}
if (!targetNode) { // not the case of same-map target node or a full node URI is used as the link
    // work out what the target node is
    if (linkText && node.link.uri.scheme !in ['menuitem', 'file', 'https', 'http']) {
        def nodeAndMapReference = new NodeAndMapReference(linkText)
        if (nodeAndMapReference.hasFreeplaneFileExtension() && nodeAndMapReference.hasNodeReference()) {
            def thisFile = node.mindMap.file
            def mapReferenceDecoded = URLDecoder.decode(nodeAndMapReference.getMapReference(), StandardCharsets.UTF_8.name())
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
    def mvc = Controller.getCurrentController().getMapViewManager() as MapViewController
    def thisMapModel = node.mindMap.delegate as MapModel
    def thisMapView = mvc.getMapView()
    def allMapViews = FP_VER >= FP_1_12_9 ? mvc.getMapViews() : mvc.getMapViewVector()
    def findOtherMapView = {
        allMapViews.find {
            try {
                it !== thisMapView && it.getMap() === thisMapModel
            } catch (MissingPropertyException ignore) { // before 1.11.8-pre01
                it !== thisMapView && it.getModel() === thisMapModel
            }
        }
    }
    def otherMapView = findOtherMapView()
    if (!otherMapView) { // open another map view if not exists
        Controller.getCurrentModeController().getMapController().createMapView(thisMapModel)
        otherMapView = findOtherMapView()
        UITools.showMessage('A new map view has been created', JOptionPane.INFORMATION_MESSAGE)
    }
    otherMapView.select()
    // based on org.freeplane.plugin.script.proxy.ControllerProxy#select(org.freeplane.api.Node)
    Controller.getCurrentModeController().getMapController().displayNode(targetNodeModel)
    Controller.getCurrentController().getSelection().selectAsTheOnlyOneSelected(targetNodeModel)
    c.statusInfo = "${this.class.simpleName}:  $linkText "
    // flash spotlight
    toggleSpotlight()
    // Timer executes its listeners on the Event Dispatch Thread (EDT), which is crucial for Swing's thread safety
    new Timer(400, { evt -> toggleSpotlight(); evt.source.stop() }).start()
} else {
    c.statusInfo = "${this.class.simpleName}:  no link found! "
}
