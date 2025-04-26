/**
 * Copyright (C) 2025  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Paste"})


import org.freeplane.api.Controller
import org.freeplane.api.Node

import javax.imageio.ImageIO
import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.image.BufferedImage

node = node as Node
c = c as Controller
def mindMapFile = node.mindMap.file

def settings = [
        customSaveDir      : mindMapFile.parent,
        imageFileNamePrefix: node.mindMap.name,
//        customSaveDir      : "${mindMapFile.parent}/images",
//        imageFileNamePrefix: '',
]

def customSaveDir = settings.customSaveDir ?: mindMapFile.parent
if (!new File(customSaveDir).canWrite()) {
    c.statusInfo = "(!) cannot write to ${customSaveDir}"
    return
}

def transferable = Toolkit.defaultToolkit.systemClipboard.getContents(null)
if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
    def bufferedImage = transferable.getTransferData(DataFlavor.imageFlavor) as BufferedImage
    def imageFileNamePrefix = settings.imageFileNamePrefix ? "${settings.imageFileNamePrefix}_" : ''
    def imageFileName = "${imageFileNamePrefix}${node.id}_${new Date().format('yyyyMMdd_HHmmssS')}.png"
    def file = new File(customSaveDir, imageFileName)
    def useCache = ImageIO.useCache
    ImageIO.useCache = false
    ImageIO.write(bufferedImage, 'png', file)
    ImageIO.useCache = useCache
    def child = node.createChild()
    child.text = imageFileName
    if (config.getProperty('links') == 'relative') {
        def filePathRelativeToMindMapParent = mindMapFile.parentFile.relativePath(file)
        def uri = makeUriRetainingRelative(filePathRelativeToMindMapParent)
        child.externalObject.uri = uri
//        child.details = "URI: $uri"
    } else {
        child.externalObject.file = file
//        child.details = "File: $file"
    }
} else {
    c.statusInfo = '(!) no image found in clipboard'
}

/**
 * File#toURI() converts fsPaths to absolute paths
 * This method works around it to allow relative fsPaths
 * @param fsPath relative or absolute
 * @return URI
 */
static URI makeUriRetainingRelative(String fsPath) {
    return makeUriRetainingRelative(new File(fsPath))
}

static URI makeUriRetainingRelative(File file) {
    if (file.absolute) {
        return file.toURI()
    } else {
        def prefixPath = file.absolutePath[0..<-file.path.size()]
        def prefixUriStr = new File(prefixPath).toURI().toString()
        def absoluteUriStr = file.toURI().toString()
        def relativeUriStr = absoluteUriStr[prefixUriStr.size()..-1]
        return relativeUriStr.toURI()
    }
}
