// Copyright (C) 2026  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Toggle"})
import org.freeplane.api.ConditionalStyle
import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.freeplane.core.util.HtmlUtils
import org.freeplane.features.map.NodeModel

import javax.swing.JOptionPane

def detailsInAttributesStyle = ''
def detailsInAttributesIcon = 'emoji-1F539'
def DETAILS = 'details'
def keepHtml = true


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
        attributes.add(DETAILS, !keepHtml || plainDetailsText.startsWith('=') ? plainDetailsText : detailsText)
        node.details = null
        if (detailsInAttributesStyle) {
            node.conditionalStyles.add(true, null, detailsInAttributesStyle, false)
        }
        if (detailsInAttributesIcon) {
            node.icons.add(detailsInAttributesIcon)
        }
    }
} else {
    if (attributeDetails) {
        node.details = attributeDetails
        attributes.removeAll(DETAILS)
        if (detailsInAttributesStyle) {
            def ncs = node.conditionalStyles
            ncs.find { ConditionalStyle it -> it.active && it.always && it.styleName == detailsInAttributesStyle && !it.last }.each { ConditionalStyle it -> it.remove() }
        }
        if (detailsInAttributesIcon) {
            node.icons.remove(detailsInAttributesIcon)
        }
    }
}
