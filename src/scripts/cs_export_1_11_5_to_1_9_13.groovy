// Copyright (C) 2024  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/CStorage"})


import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools

import javax.swing.*
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

node = node as Node
c = c as Controller

def mmFile = node.mindMap.file
String _confluence_storage_output_dir = config.getProperty('_confluence_storage_output_dir', 'ask')
def outputDir = (_confluence_storage_output_dir == 'ask') ? askForDirectory(mmFile) : new File(_confluence_storage_output_dir)
if (outputDir === null)
    return
if (!outputDir.exists()) {
    UITools.showMessage("Output directory \n${outputDir}\ndoesn't exist", JOptionPane.ERROR_MESSAGE)
    return
}
if (outputDir == mmFile.parentFile) {
    UITools.showMessage("Output directory is the same as mind-map directory \n${outputDir}", JOptionPane.ERROR_MESSAGE)
    return
}
def outputFile = new File(outputDir, mmFile.name)

def xsltFile = new File(c.userDirectory, 'resources/export_mm_1_11_5_to_1_9_13.xslt')
if (!xsltFile.exists()) {
    UITools.showMessage("${xsltFile} doesn't exist", JOptionPane.ERROR_MESSAGE)
    return
}

//if (outputFile.exists()) {
//    def result = UITools.showConfirmDialog(node.delegate, "Overwrite ${outputFile}?", 'Confrm Export', JOptionPane.OK_CANCEL_OPTION)
//    if (result != JOptionPane.OK_OPTION)
//        return
//}

outputFile.withOutputStream {
    TransformerFactory.newInstance()
            .newTransformer(new StreamSource(xsltFile))
            .transform(new StreamSource(mmFile), new StreamResult(it))
}
c.statusInfo = "exported to ${outputFile}"

static File askForDirectory(File suggestedFile = null) {
    final fileChooser = new JFileChooser()
    fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
    fileChooser.multiSelectionEnabled = false
    if (suggestedFile)
        fileChooser.selectedFile = suggestedFile
    final returnVal = fileChooser.showOpenDialog(null)
    if (returnVal != JFileChooser.APPROVE_OPTION) {
        return
    }
    return fileChooser.getSelectedFile()
}
