// Copyright (C) 2026  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Toggle"})
import org.freeplane.api.ConditionalStyle
import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.freeplane.core.util.HtmlUtils
import org.freeplane.features.map.NodeModel

import javax.swing.JOptionPane

def DETAILS_IN_ATTRIBUTES_STYLE = ''
def DETAILS_IN_ATTRIBUTES_ICON = 'emoji-1F539'
def DETAILS = 'details'
def KEEP_HTML = true


node = node as Node
def attributes = node.attributes
def detailsText = node.detailsText
def attributeDetails = attributes.getFirst(DETAILS)
if (detailsText) {
    if (attributeDetails) {
        def nodeModel = node.delegate as NodeModel
        UITools.showConfirmDialog(nodeModel, "The attribute '${DETAILS}' already contains data", 'Toggle Hide Details in Attributes', JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE)
    } else {
        def plainDetailsText = HtmlUtils.htmlToPlain(detailsText)
        attributes.add(DETAILS, !KEEP_HTML || plainDetailsText.startsWith('=') ? plainDetailsText : detailsText)
        node.details = null
        if (DETAILS_IN_ATTRIBUTES_STYLE) {
            node.conditionalStyles.add(true, null, DETAILS_IN_ATTRIBUTES_STYLE, false)
        }
        if (DETAILS_IN_ATTRIBUTES_ICON) {
            node.icons.add(DETAILS_IN_ATTRIBUTES_ICON)
        }
    }
} else {
    if (attributeDetails) {
        node.details = attributeDetails
        attributes.removeAll(DETAILS)
        if (DETAILS_IN_ATTRIBUTES_STYLE) {
            def ncs = node.conditionalStyles
            ncs.find { ConditionalStyle it -> it.active && it.always && it.styleName == DETAILS_IN_ATTRIBUTES_STYLE && !it.last }.each { ConditionalStyle it -> it.remove() }
        }
        if (DETAILS_IN_ATTRIBUTES_ICON) {
            node.icons.remove(DETAILS_IN_ATTRIBUTES_ICON)
        }
    }
}
