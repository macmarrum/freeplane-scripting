/* if map has many views, go to the previous one, else go to the previous map
 *
 * Copyright (C) 2025, 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/GoTo"})

import org.freeplane.core.util.MenuUtils
import org.freeplane.features.mode.Controller
import org.freeplane.view.swing.map.MapViewController

import javax.swing.Timer


static def toggleSpotlight() {
    // org.freeplane.features.styles.SetBooleanMapViewPropertyAction.actionPerformed
    def propertyName = 'spotlight'
    def mapViewComponent = Controller.getCurrentController().getMapViewManager().getMapViewComponent()
    final Boolean value = Boolean.TRUE.equals(mapViewComponent.getClientProperty(propertyName))
    boolean newValue = !value.booleanValue()
    mapViewComponent.putClientProperty(propertyName, newValue)
}

static def flashSpotlight() {
    toggleSpotlight()
    // Timer executes its listeners on the Event Dispatch Thread (EDT), which is crucial for Swing's thread safety
    new Timer(400, { evt -> toggleSpotlight(); evt.source.stop() }).start()
}


// inspired by org.freeplane.plugin.script.proxy.ControllerProxy#getMapViewManager
def mvc = Controller.currentController.mapViewManager as MapViewController
def thisMap = mvc.map
def thisMapViewNames = mvc.maps.findAll { it.value == thisMap }.collect { it.key }
if (thisMapViewNames.size() > 1) {
    def thisViewName = mvc.mapView.name
    def thisViewNameIdx = thisMapViewNames.indexOf(thisViewName)
    if (thisViewNameIdx == -1)
        return
    String prevViewName
    if (thisViewNameIdx == 0)
        prevViewName = thisMapViewNames[thisMapViewNames.size() - 1]
    else
        prevViewName = thisMapViewNames[thisViewNameIdx - 1]
    println(":: prevViewName: $prevViewName")
    mvc.changeToMapView(prevViewName)
    flashSpotlight()
} else {
    MenuUtils.executeMenuItems(['NavigationPreviousMapAction'])
}
