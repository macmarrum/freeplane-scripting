// Copyright (C) 2026  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Toggle"})
import org.freeplane.api.ConditionalStyle
import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.freeplane.core.util.HtmlUtils
import org.freeplane.core.util.TextUtils
import org.freeplane.features.map.NodeModel

import javax.swing.JOptionPane

def DETAILS_IN_ATTRIBUTES_STYLE = ''
def DETAILS_IN_ATTRIBUTES_ICON = 'emoji-1F539'
def DETAILS = 'details'
def KEEP_HTML = true

c = c as Controller

static findSinglesAndCloneLeaders(Iterable<Node> nodes) {
    if (nodes.size() == 1)
        return nodes
    def singlesAndLeaders = new ArrayList<Node>()
    def subtrees = new HashSet<Node>()
    def clones = new HashSet<Node>()
    nodes.each { Node n ->
        if (n !in subtrees) { // not a tree clone or a clone leader
            def nodesSharingContentAndSubtree = n.nodesSharingContentAndSubtree
            subtrees.addAll(nodesSharingContentAndSubtree)
            if (n !in clones) { // not a content clone or a clone leader
                singlesAndLeaders << n
                clones.addAll(n.nodesSharingContent - nodesSharingContentAndSubtree)
            }
        }
    }
    return singlesAndLeaders
}

def errorNodes = new ArrayList<Node>()
findSinglesAndCloneLeaders(c.selecteds).each { Node node ->
    def attributes = node.attributes
    def detailsText = node.detailsText
    def attributeDetails = attributes.getFirst(DETAILS)
    if (detailsText) {
        if (attributeDetails) {
            errorNodes << node
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
}
if (errorNodes) {
    def nodeModel = c.selected.delegate as NodeModel
    def message = "The attribute '${DETAILS}' already contains data!\n${errorNodes.size()} nodes in error — IDs copied to the clipboard"
    UITools.showConfirmDialog(nodeModel, message, 'Toggle Hide Details in Attributes', JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE)
    TextUtils.copyToClipboard(errorNodes.collect { "${it.id}" }.join(', '))
}
