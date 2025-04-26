/**
 * Copyright (C) 2025  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Create"})


import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools

import java.util.concurrent.TimeUnit

node = node as Node
def mindMapFile = node.mindMap.file

def settings = [
        mermaidCli         : '/usr/bin/mmdc',
//        mermaidCli         : /C:\Users\Mac\AppData\Roaming\npm\mmdc.cmd/,
//        mermaidCliExtraArgs: ['-t', 'dark'],
//        mermaidCliTimeoutSec: 30,
        mermaidCliVerbose  : false, // in the mind map, create a branch representing mermaid-cli arguments
        mermaidNodePart    : 'auto', // [auto|core|note|details]
        customSaveDir      : mindMapFile.parent,
        imageFileNamePrefix: node.mindMap.name,
//        customSaveDir      : "${mindMapFile.parent}/my_images",
//        imageFileNamePrefix: '',
]

def mermaidCliFile = new File(settings.mermaidCli as String)
if (!mermaidCliFile.canExecute()) {
    UITools.showMessage("cannot execute ${mermaidCliFile}", 2)
    return
}

def mermaidNodePart = settings.get('mermaidNodePart', 'auto')
def text = switch (mermaidNodePart) {
    case 'auto' -> node.transformedText ?: node.note?.text ?: node.details?.text
    case 'core' -> node.transformedText
    case 'note' -> node.note?.text
    case 'details' -> node.details?.text
    default -> ''
}

if (text) {
    def customSaveDir = settings.get('customSaveDir', mindMapFile.parent) as String
    def imageFileNamePrefix = settings['imageFileNamePrefix'] ? "${settings.imageFileNamePrefix}_" : ''
    def imageFileName = "${imageFileNamePrefix}${node.id}.png"
    def imageFile = new File(customSaveDir, imageFileName)
    def mermaidFile = new File("${imageFile}.mermaid")
    mermaidFile.setText(text, 'UTF-8')
    def mermaidCliExtraArgs = settings.get('mermaidCliExtraArgs', [])
    def mermaidCliTimeoutSec = settings.mermaidCliTimeoutSec as Integer ?: 30
    def args = [mermaidCliFile, '-i', mermaidFile, '-o', imageFile, *mermaidCliExtraArgs]
    if (settings['mermaidCliVerbose']) {
        def child = node.createChild(new Date())
        child.format = 'yyyy-MM-dd HH:mm:ss'
        child.createChild(args.join(' '))
    }
    args.execute().waitFor(mermaidCliTimeoutSec, TimeUnit.SECONDS)
    mermaidFile.delete()
    if (config.getProperty('links') == 'relative') {
        def imageFilePathRelativeToMindMapParent = mindMapFile.parentFile.relativePath(imageFile)
        def imageUri = makeUriRetainingRelative(imageFilePathRelativeToMindMapParent)
        node.externalObject.uri = imageUri
    } else {
        node.externalObject.file = imageFile
    }
} else {
    UITools.showMessage("no Mermaid definition in ${mermaidNodePart == 'auto' ? 'core, note, details' : mermaidNodePart}", 2)
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
