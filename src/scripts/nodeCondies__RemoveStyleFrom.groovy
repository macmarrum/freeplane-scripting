// Copyright (C) 2024, 2025  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/NodeCondies"})

import io.github.macmarrum.swing.ComboBoxDialog
import org.freeplane.api.ConditionalStyle
import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.freeplane.features.styles.IStyle
import org.freeplane.features.styles.StyleTranslatedObject
import org.freeplane.plugin.script.proxy.AConditionalStyleProxy

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

c = c as Controller
def selectedSinglesAndCloneLeaders = findSinglesAndCloneLeaders(c.selecteds)

def stylesForComboBox = new LinkedList<IStyle>()
def styleNamesForComboBox = new LinkedList<String>()

IStyle style
String displayName

selectedSinglesAndCloneLeaders.each { Node n ->
    for (ConditionalStyle condi in n.conditionalStyles) {
        if (condi.active && condi.always && !condi.last) {
            style = (condi as AConditionalStyleProxy).style
            stylesForComboBox << style
            displayName = style as String
            styleNamesForComboBox << (style instanceof StyleTranslatedObject ? "$displayName   (${style.object})".toString() : displayName)
        }
    }
}

def onEntryAccepted = { JComboBox<String> comboBox ->
    int idx = styleNamesForComboBox.indexOf(comboBox.selectedItem)
    style = stylesForComboBox[idx]
    for (Node n in selectedSinglesAndCloneLeaders) {
        def ncs = n.conditionalStyles
        ncs.find { ConditionalStyle it ->
            it.active && it.always && (it as AConditionalStyleProxy).style == style && !it.last
        }.each { ConditionalStyle it ->
            it.remove()
        }
    }
}

if (styleNamesForComboBox.isEmpty()) {
    UITools.showMessage('No condi styles to remove', JOptionPane.WARNING_MESSAGE)
} else {
    def uniqueStyleNamesForComboBox = (selectedSinglesAndCloneLeaders.size() == 1 ? styleNamesForComboBox : new TreeSet<String>(styleNamesForComboBox)) as String[]
    SwingUtilities.invokeLater {
        new ComboBoxDialog(UITools.currentFrame, 'Remove style', null, uniqueStyleNamesForComboBox, onEntryAccepted, true)
    }
}
