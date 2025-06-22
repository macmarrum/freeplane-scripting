// Copyright (C) 2024, 2025  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/NodeCondies"})


import io.github.macmarrum.swing.AutoCompletionComboBoxEnricher
import io.github.macmarrum.swing.ComboBoxDialog
import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.freeplane.features.map.MapModel
import org.freeplane.features.map.NodeModel
import org.freeplane.features.styles.IStyle
import org.freeplane.features.styles.MapStyleModel
import org.freeplane.features.styles.StyleTranslatedObject
import org.freeplane.plugin.script.proxy.AConditionalStyleProxy
import org.freeplane.plugin.script.proxy.NodeConditionalStyleProxy

import javax.swing.*

/* NOTE
 * Freeplane can have LOCALIZED_STYLE_REF and STYLE_REF with the same value, e.g. "styles.topic"
 * The first one is Freeplane-internal and translated, the second is user-defined and not translated
 * They differ in IStyle, but Scripting API (node.style.name) cannot tell them apart
 * Therefore it's important to operate on IStyles rather than on their String representations
 */

static findSinglesAndCloneLeaders(Iterable<Node> nodes) {
    if (nodes.size() == 1)
        return nodes
    def singlesAndLeaders = new LinkedList<Node>()
    def subtrees = new HashSet<Node>()
    def clones = new HashSet<Node>()
    for (Node n in nodes) {
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

node = node as Node
c = c as Controller
def selectedSinglesAndCloneLeaders = findSinglesAndCloneLeaders(c.selecteds)

List<IStyle> currentConditionalStyles
if (selectedSinglesAndCloneLeaders.size() == 1) {
    def ncsItems = node.conditionalStyles.collect()
    currentConditionalStyles = new ArrayList<IStyle>(ncsItems.size())
    for (item in ncsItems) {
        if (item.active && item.always && !item.last)
            currentConditionalStyles << (item as AConditionalStyleProxy).style
    }
} else {
    currentConditionalStyles = Collections.emptyList()
}

// MapStyleModel::getNodeStyles() contains user-def and automatic-level styles plus DEFAULT and FLOATING
def stylesForComboBox = MapStyleModel.getExtension(node.mindMap.delegate as MapModel).nodeStyles.findAll {
    it !in currentConditionalStyles && it !== MapStyleModel.DEFAULT_STYLE
}
def styleNamesForComboBox = new String[stylesForComboBox.size()]
String displayName
stylesForComboBox.eachWithIndex { style, i ->
    displayName = style as String
    styleNamesForComboBox[i] = style instanceof StyleTranslatedObject ? "$displayName   (${style.object})".toString() : displayName
}

def onEntryAccepted = { JComboBox<String> comboBox, JCheckBox _ ->
    def style = stylesForComboBox[comboBox.selectedIndex]
    for (n in selectedSinglesAndCloneLeaders) {
        def ncs = n.conditionalStyles
        if (!ncs.find { it.active && it.always && (it as AConditionalStyleProxy).style == style && !it.last }) {
            // use NodeConditionalStyleProxy to add iStyle,
            // otherwise there's a risk that a user-defined style will be added instead of a predefined one
            // if both have the same reference name like styles.topic or AutomaticLayout.level.root
            // -- see NodeStyleProxy::styleByName
            ncs.add(new NodeConditionalStyleProxy(n.delegate as NodeModel, true, null, style, false))
        }
    }
}

SwingUtilities.invokeLater {
    new ComboBoxDialog(UITools.currentFrame, AutoCompletionComboBoxEnricher, 'Add style', styleNamesForComboBox, onEntryAccepted)
}
