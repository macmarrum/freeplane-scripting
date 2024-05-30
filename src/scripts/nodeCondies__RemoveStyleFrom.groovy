/**
 * Copyright (C) 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/NodeCondies"})

import io.github.macmarrum.swing.AutoCompletionComboDialog
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

def c = c as Controller

def selectedNodes = c.selecteds

def stylesForComboBox = new LinkedList<IStyle>()
def styleNamesForComboBox = new LinkedList<String>()

IStyle style
String displayName

selectedNodes.each { Node n ->
    n.conditionalStyles.each { ConditionalStyle it ->
        if (it.active && it.always && !it.last) {
            style = (it as AConditionalStyleProxy).style
            stylesForComboBox << style
            displayName = style as String
            styleNamesForComboBox << (style instanceof StyleTranslatedObject ? "$displayName   (${style.object})".toString() : displayName)
        }
    }
}

def onEntryAccepted = { JComboBox<String> comboBox ->
    int idx = styleNamesForComboBox.indexOf(comboBox.selectedItem)
    style = stylesForComboBox[idx]
    selectedNodes.each { Node n ->
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
    def uniqueStyleNamesForComboBox = selectedNodes.size() == 1 ? styleNamesForComboBox : new TreeSet<String>(styleNamesForComboBox)
    SwingUtilities.invokeLater {
        new AutoCompletionComboDialog(UITools.currentFrame, 'Remove style', uniqueStyleNamesForComboBox.toArray(new String[0]), onEntryAccepted)
    }
}
