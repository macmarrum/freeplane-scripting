/*
 * Copyright (C) 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Toggle"})
import org.freeplane.core.resources.ResourceController
import org.freeplane.features.mode.Controller

import javax.swing.Timer
import java.awt.Color

final VIEW_MODE = 'view_mode'
final SELECTED_NODE_BUBBLE_COLOR = 'standardselectednoderectanglecolor'

def controller = ResourceController.resourceController
def viewMode = controller.getBooleanProperty(VIEW_MODE)
def selectedNodeBubbleColor = controller.getProperty(SELECTED_NODE_BUBBLE_COLOR)
controller.setProperty(VIEW_MODE, "${!viewMode}")
if (selectedNodeBubbleColor) {
    controller.setProperty(SELECTED_NODE_BUBBLE_COLOR, flipColorHue(selectedNodeBubbleColor))
}
flashSpotlight()

static String flipColorHue(String colorAsHex) {
    def color = Color.decode(colorAsHex.startsWith('#') ? colorAsHex : '#' + colorAsHex)
    float[] hsb = Color.RGBtoHSB(color.red, color.green, color.blue, null)
    hsb[0] = (hsb[0] + 0.5f) % 1.0f  // flip hue by 180°
    def flipped = Color.getHSBColor(hsb[0], hsb[1], hsb[2])
    return sprintf('#%02x%02x%02x', flipped.red, flipped.green, flipped.blue)
}

static def flashSpotlight() {
    toggleSpotlight()
    // Timer executes its listeners on the Event Dispatch Thread (EDT), which is crucial for Swing's thread safety
    new Timer(400, { evt -> toggleSpotlight(); evt.source.stop() }).start()
}

static def toggleSpotlight() {
    // org.freeplane.features.styles.SetBooleanMapViewPropertyAction.actionPerformed
    def propertyName = 'spotlight'
    def mapViewComponent = Controller.getCurrentController().getMapViewManager().getMapViewComponent()
    final Boolean value = Boolean.TRUE.equals(mapViewComponent.getClientProperty(propertyName))
    boolean newValue = !value.booleanValue()
    mapViewComponent.putClientProperty(propertyName, newValue)
}
