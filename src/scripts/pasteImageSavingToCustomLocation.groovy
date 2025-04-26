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

def settings = [
        customSaveDir      : node.mindMap.file.parent,
//        customSaveDir      : "${node.mindMap.file.parent}/Mind-map_screenshots",
        imageFileNamePrefix: node.mindMap.name,
//        imageFileNamePrefix: '',
]

def transferable = Toolkit.defaultToolkit.systemClipboard.getContents(null)
if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
    def bufferedImage = transferable.getTransferData(DataFlavor.imageFlavor) as BufferedImage
    def customSaveDir = settings.customSaveDir ?: node.mindMap.file.parent
    def imageFileNamePrefix = settings.imageFileNamePrefix ? "${settings.imageFileNamePrefix}_" : ''
    def imageFileName = "${imageFileNamePrefix}${node.id}_${new Date().format('yyyyMMdd_HHmmssS')}.png"
    def file = new File(customSaveDir, imageFileName)
    def useCache = ImageIO.useCache
    ImageIO.useCache = false
    ImageIO.write(bufferedImage, 'png', file)
    ImageIO.useCache = useCache
    def child = node.createChild()
    child.text = imageFileName
    child.externalObject.file = file
} else {
    c.statusInfo = '(!) no image found in clipboard'
}
