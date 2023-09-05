// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
/*
 * Copyright (C) 2023  macmarrum
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


import io.github.macmarrum.freeplane.MindMapComparator
import org.freeplane.core.ui.components.UITools
import org.freeplane.plugin.script.proxy.ScriptUtils

import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter

static File askForFile() {
    final fileChooser = new JFileChooser()
    fileChooser.dialogTitle = 'Select the original mind map (i.e. older version)'
    fileChooser.multiSelectionEnabled = false
    fileChooser.fileFilter = new FileNameExtensionFilter('Mind map', 'mm')
    fileChooser.currentDirectory = ScriptUtils.node().mindMap.file.parentFile
    final returnVal = fileChooser.showOpenDialog(UITools.currentRootComponent)
    if (returnVal != JFileChooser.APPROVE_OPTION) {
        return
    }
    return fileChooser.getSelectedFile()
}

if (!config.getBooleanProperty('assignsNodeDependantStylesToNewConnectors'))
    UITools.showMessage('Enable "Preferences…->Defaults->Connectors->Assigns node dependant styles to new connectors" and try again', JOptionPane.WARNING_MESSAGE)
else {
    def node = ScriptUtils.node()
    def c = ScriptUtils.c()
    def oldFile = askForFile()
    if (oldFile) {
        def oldMindMap = c.mapLoader(oldFile).mindMap
        def mindMap = c.mapLoader(node.mindMap.file).unsetMapLocation().withView().mindMap
        MindMapComparator.compare(oldMindMap, mindMap)
    }
}
