/**
 * Copyright (C) 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/CondiStyle"})

import io.github.macmarrum.swing.AutoCompletionComboDialog
import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.freeplane.features.styles.MapStyleModel
import org.freeplane.features.styles.StyleTranslatedObject

import javax.swing.*

def node = node as Node
def c = c as Controller

def selectedNodes = c.selecteds
def styleNames = new LinkedList<String>()
def styleDisplayNames = new LinkedList<String>()
def styleDisplayNameToCount = new HashMap<String, Integer>()
List<String> currentConditionalStyleNames
if (selectedNodes.size() == 1)
    currentConditionalStyleNames = node.conditionalStyles.collect { it.active && it.always && !it.last ? it.styleName : null }
else
    currentConditionalStyleNames = Collections.emptyList()
def technicalStylesToIgnore = ['default', 'defaultstyle.details', 'defaultstyle.attributes', 'defaultstyle.note', 'defaultstyle.selection']
MapStyleModel.getExtension(node.mindMap.delegate).styles.each {
    def displayName = it as String
    def styleName = it instanceof StyleTranslatedObject ? (it as StyleTranslatedObject).object as String : displayName
    if (styleName !in currentConditionalStyleNames && styleName !in technicalStylesToIgnore) {
        styleDisplayNames << displayName
        styleDisplayNameToCount.compute(displayName) { k, count -> count == null ? 1 : ++count }
        styleNames << styleName
    }
}

def styleList = new ArrayList<String>(styleNames.size())
styleDisplayNames.eachWithIndex { String displayName, int i ->
    def displayNamePlus = styleDisplayNameToCount[displayName] == 1 ? displayName : "${displayName} --${styleNames[i]}" as String
    styleList << displayNamePlus
}
def onEntryAccepted = { JComboBox<String> comboBox ->
    String styleName = styleNames[comboBox.selectedIndex]
    (c as Controller).selecteds.each { Node n ->
        def ncs = n.conditionalStyles
        if (!ncs.find { it.active && it.always && it.styleName == styleName && !it.last })
            ncs.add(true, null, styleName, false)
    }
}
SwingUtilities.invokeLater {
    new AutoCompletionComboDialog(UITools.currentFrame, 'Add style', styleList.toArray(new String[0]), onEntryAccepted)
}
