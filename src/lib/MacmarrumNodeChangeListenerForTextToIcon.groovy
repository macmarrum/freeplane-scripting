/**
 * Copyright (C) 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// https://github.com/freeplane/freeplane/discussions/2008


import org.freeplane.api.Node
import org.freeplane.api.NodeChangeListener
import org.freeplane.api.NodeChanged
import org.freeplane.features.icon.IconController
import org.freeplane.features.icon.UserIcon
import org.freeplane.features.icon.mindmapmode.IconAction
import org.freeplane.features.icon.mindmapmode.MIconController

import static org.freeplane.api.NodeChanged.ChangedElement

class MacmarrumNodeChangeListenerForTextToIcon implements NodeChangeListener {
    public static canReact = true
    public static final MIconController mic = IconController.controller as MIconController
    public static final availableUserIcons = mic.getIconActions { it instanceof UserIcon }.collect { a -> (a as IconAction).mindIcon.name }

    static void convertTextToIcon(Node n) {
        def text = n.text
        if (text in availableUserIcons) {
            n.icons.add(text)
            n.text = null
        }
    }

    void nodeChanged(NodeChanged event) {
        /* enum ChangedElement {TEXT, DETAILS, NOTE, ICON, ATTRIBUTE, FORMULA_RESULT, UNKNOWN} */
        if (!canReact) {
            return
        }
        canReact = false
        switch (event.changedElement) {
            case ChangedElement.TEXT -> convertTextToIcon(event.node)
        }
        canReact = true
    }
}
