/*
 * Copyright (C) 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Toggle"})
import org.freeplane.core.resources.ResourceController
import org.freeplane.features.mode.Controller

import javax.swing.Timer

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
    // Remove '#' if present
    String hex = colorAsHex.replaceFirst('^#', '')
    // Convert hex to RGB
    int r = Integer.parseInt(hex.substring(0, 2), 16)
    int g = Integer.parseInt(hex.substring(2, 4), 16)
    int b = Integer.parseInt(hex.substring(4, 6), 16)
    // Normalize RGB to 0-1 range
    float rNorm = r / 255.0f
    float gNorm = g / 255.0f
    float bNorm = b / 255.0f
    // Convert RGB to HSV
    float max = [rNorm, gNorm, bNorm].max()
    float min = [rNorm, gNorm, bNorm].min()
    float delta = max - min
    // Calculate Hue (0-360)
    float hue
    if (delta == 0) {
        hue = 0
    } else if (max == rNorm) {
        hue = (60 * (((gNorm - bNorm) / delta) % 6) + 360) % 360
    } else if (max == gNorm) {
        hue = (60 * (((bNorm - rNorm) / delta) + 2)) % 360
    } else {
        hue = (60 * (((rNorm - gNorm) / delta) + 4)) % 360
    }
    // Calculate Saturation (0-1)
    float saturation = max == 0 ? 0 : delta / max
    // Calculate Value (0-1)
    float value = max
    // Flip hue by 180 degrees
    hue = (hue + 180) % 360
    // Convert HSV back to RGB
    float c = value * saturation
    float hPrime = hue / 60.0f
    float x = c * (1 - Math.abs(hPrime % 2 - 1))
    float rPrime, gPrime, bPrime
    if (hPrime < 1) {
        rPrime = c; gPrime = x; bPrime = 0
    } else if (hPrime < 2) {
        rPrime = x; gPrime = c; bPrime = 0
    } else if (hPrime < 3) {
        rPrime = 0; gPrime = c; bPrime = x
    } else if (hPrime < 4) {
        rPrime = 0; gPrime = x; bPrime = c
    } else if (hPrime < 5) {
        rPrime = x; gPrime = 0; bPrime = c
    } else {
        rPrime = c; gPrime = 0; bPrime = x
    }
    float m = value - c
    int rOut = Math.round((rPrime + m) * 255) as int
    int gOut = Math.round((gPrime + m) * 255) as int
    int bOut = Math.round((bPrime + m) * 255) as int
    // Convert back to hex
    return String.format('#%02x%02x%02x', rOut, gOut, bOut)
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
