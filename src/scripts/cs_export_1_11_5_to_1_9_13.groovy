// Copyright (C) 2024  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/CStorage"})
import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools

import javax.swing.*
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

File mmFile = (node as Node).mindMap.file
def outputDir = new File(config.getProperty('confluence_storage_output_dir'))
if (!outputDir.exists()) {
    UITools.showMessage("Output Directory \n${outputDir}\ndoesn't exist", JOptionPane.ERROR_MESSAGE)
    return
}
if (outputDir == mmFile.parent) {
    UITools.showMessage("Output Directory is the same as mind-map parent \n${outputDir}", JOptionPane.ERROR_MESSAGE)
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
