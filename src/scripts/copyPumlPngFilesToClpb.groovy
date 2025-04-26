/** Dumps to tmpDir PlantUML diagrams found in selected nodes and copies those files to clipboard
 * Copyright (C) 2025  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Copy"})

package io.github.macmarrum.freeplane

import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.jsoup.Jsoup

import javax.swing.*
import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException

settings = [
        nth : null, // nth node's image to be copied, starting from 1; null for all
        warn: true, // whether to show a warning dialog
]

c = c as Controller
def tmpDir = System.getProperty('java.io.tmpdir')
def plantUmlPngFiles = new ArrayList<File>()
for (Node node_ in c.selecteds) {
    if (node_.format != 'markdownPatternFormat')
        continue
    def html = node_.transformedText
    def doc = Jsoup.parse(html)
    int num = 1
    for (div in doc.select('div.plantuml:has(img)')) {
        if (!settings.nth || num == settings.nth) {
            def img = div.selectFirst('img')
            def src = img.attr('src') as String
            def base64 = src.split(',')[1]
            def bytes = Base64.decoder.decode(base64)
            def plantUmlPngFile = new File(tmpDir, "puml-freeplane-${node_.id}-${num}.png")
            plantUmlPngFile.bytes = bytes
            plantUmlPngFiles << plantUmlPngFile
            if (num == settings.nth)
                break
        }
        num++
    }
}
if (plantUmlPngFiles) {
    def fileTransferable = new io.github.macmarrum.freeplane.FilesTransferable(plantUmlPngFiles)
    Toolkit.defaultToolkit.systemClipboard.setContents(fileTransferable, null)
    c.statusInfo = "${plantUmlPngFiles.size()} PlantUML PNG files copied to clipboard"
} else {
    def msg = 'no PlantUML PNG files copied to clipboard'
    if (settings.warn)
        UITools.showMessage(msg, JOptionPane.WARNING_MESSAGE)
    else
        c.statusInfo = "(!) $msg"
}

class FilesTransferable implements Transferable {
    private static CRLF = '\r\n'
    private static LF = '\n'
    private static uriListFlavor = new DataFlavor('text/uri-list;class=java.lang.String')
    private Collection<File> files

    FilesTransferable(Collection<File> files) {
        this.files = files
    }

    @Override
    DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DataFlavor.javaFileListFlavor, uriListFlavor, DataFlavor.stringFlavor}
    }

    @Override
    boolean isDataFlavorSupported(DataFlavor flavor) {
        return DataFlavor.javaFileListFlavor == flavor || uriListFlavor == flavor || DataFlavor.stringFlavor == flavor
    }

    @Override
    Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (DataFlavor.javaFileListFlavor == flavor) return files.toList()
        else if (uriListFlavor == flavor) return files.collect() { it.toURI() }.join(CRLF) // rfc2483
        else if (DataFlavor.stringFlavor == flavor) return files.collect { it.path }.join(LF)
        else throw new UnsupportedFlavorException(flavor)
    }
}
