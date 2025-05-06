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

c = c as Controller
node = node as Node
def mindMapFile = node.mindMap.file

HashMap<String, Object> settings = [
        customSaveDir      : mindMapFile.parent,
        imageFileNamePrefix: node.mindMap.name,
//        customSaveDir      : "${mindMapFile.parent}/my_images",
//        imageFileNamePrefix: '',
        asChild            : true,
]

String customSaveDir = settings.get('customSaveDir', mindMapFile.parent)
if (!new File(customSaveDir).canWrite()) {
    c.statusInfo = "(!) cannot write to ${customSaveDir}"
    return
}

def transferable = Toolkit.defaultToolkit.systemClipboard.getContents(null)
if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
    def bufferedImage = transferable.getTransferData(DataFlavor.imageFlavor) as BufferedImage
    String imageFileNamePrefix = settings['imageFileNamePrefix'] ? "${settings.imageFileNamePrefix}-" : ''
    def imageFileName = "${imageFileNamePrefix}${node.id}-${new Date().format('yyyyMMdd_HHmmssS')}.png"
    def imageFile = new File(customSaveDir, imageFileName)
    def useCache = ImageIO.useCache
    ImageIO.useCache = false
    ImageIO.write(bufferedImage, 'png', imageFile)
    ImageIO.useCache = useCache
    Node n
    if (settings.getOrDefault('asChild', true)) {
        n = node.createChild()
        n.text = imageFileName
    } else {
        n = node
    }
    if (config.getProperty('links') == 'relative') {
        def filePathRelativeToMindMapParent = mindMapFile.parentFile.relativePath(imageFile)
        def imageUri = makeUriRetainingRelative(filePathRelativeToMindMapParent)
        n.externalObject.uri = imageUri
    } else {
        n.externalObject.file = imageFile
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
