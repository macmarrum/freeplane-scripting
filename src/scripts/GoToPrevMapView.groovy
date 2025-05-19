/**
 * Copyright (C) 2025  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/GoTo"})

import org.freeplane.features.mode.Controller
import org.freeplane.view.swing.map.MapViewController


// based on org.freeplane.plugin.script.proxy.ControllerProxy#getMapViewManager
def mvc = Controller.currentController.mapViewManager as MapViewController
mvc.previousMapView()
