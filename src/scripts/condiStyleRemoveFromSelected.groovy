/**
 * Copyright (C) 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/CondiStyle"})

import io.github.macmarrum.swing.AutoCompletionComboDialog
import org.freeplane.api.ConditionalStyle
import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.freeplane.features.map.MapModel
import org.freeplane.features.styles.MapStyleModel
import org.freeplane.features.styles.StyleTranslatedObject

import javax.swing.*

def node = node as Node
def c = c as Controller

def selectedNodes = c.selecteds

def mapStyleNames = new LinkedList<String>()
def mapStyleDisplayNames = new LinkedList<String>()
def mapStyleDisplayNameToCount = new HashMap<String, Integer>()
MapStyleModel.getExtension(node.mindMap.delegate as MapModel).styles.each {
    def displayName = it as String
    def styleName = it instanceof StyleTranslatedObject ? (it as StyleTranslatedObject).object as String : displayName
    mapStyleDisplayNames << displayName
    mapStyleDisplayNameToCount.compute(displayName) { k, count -> count == null ? 1 : ++count }
    mapStyleNames << styleName
}

def nodeCondiStyleDisplayNameSet = new TreeSet<String>()

int index
selectedNodes.each { Node n ->
    n.conditionalStyles.each { ConditionalStyle it ->
        if (it.active && it.always && !it.last) {
            index = mapStyleNames.indexOf(it.styleName)
            nodeCondiStyleDisplayNameSet << (index != -1 ? mapStyleDisplayNames[index] : it.styleName)
        }
    }
}

def styleList = new ArrayList<String>(nodeCondiStyleDisplayNameSet)

def onEntryAccepted = { JComboBox<String> comboBox ->
    int idx = mapStyleDisplayNames.indexOf(comboBox.selectedItem)
    String styleName = mapStyleNames[idx]
    selectedNodes.each { Node n ->
        def ncs = n.conditionalStyles
        ncs.find { ConditionalStyle it ->
            it.active && it.always && it.styleName == styleName && !it.last
        }.each { ConditionalStyle it ->
            it.remove()
        }
    }
}

if (styleList.empty) {
    UITools.showMessage('No condi styles to remove', JOptionPane.WARNING_MESSAGE)
} else {
    SwingUtilities.invokeLater {
        new AutoCompletionComboDialog(UITools.currentFrame, 'Remove style', styleList.toArray(new String[0]), onEntryAccepted)
    }
}
