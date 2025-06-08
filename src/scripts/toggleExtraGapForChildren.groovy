// Copyright (C) 2025  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Toggle"})
import org.freeplane.core.resources.ResourceController


def _fixed_extra_gap_for_children = '_fixed_extra_gap_for_children'
def resourceController = ResourceController.resourceController
def vGap = resourceController.getIntProperty(_fixed_extra_gap_for_children, -1) == -1 ? 0 : -1
resourceController.setProperty(_fixed_extra_gap_for_children, vGap)
