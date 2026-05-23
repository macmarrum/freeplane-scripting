/*
 * Copyright (C) 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Toggle"})
import org.freeplane.core.resources.ResourceController

final VIEW_MODE = 'view_mode'
def controller = ResourceController.resourceController
def viewMode = controller.getBooleanProperty(VIEW_MODE)
controller.setProperty(VIEW_MODE, "${!viewMode}")
