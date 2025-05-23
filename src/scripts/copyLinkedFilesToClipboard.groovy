/**
 * Copyright (C) 2023, 2025  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Copy"})
// https://github.com/freeplane/freeplane/issues/1376


import org.apache.commons.lang.SystemUtils
import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.plugin.script.proxy.ScriptUtils

import java.awt.*
import java.awt.datatransfer.*

class FileTransferable implements Transferable {
    private static CRLF = '\r\n'
    private static LF = '\n'
    private static uriListFlavor = new DataFlavor('text/uri-list;class=java.lang.String')
    private Collection<File> files

    FileTransferable(Collection<File> files) {
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
    Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return switch (flavor) {
            case DataFlavor.javaFileListFlavor -> files.toList()
            case uriListFlavor -> files.collect() { it.toURI() }.join(CRLF) // rfc2483
            case DataFlavor.stringFlavor -> files.collect { it.path }.join(LF)
            default -> throw new UnsupportedFlavorException(flavor)
        }
    }
}

/**
 * A modified version of org.freeplane.plugin.script.proxy.LinkProxy#getFile()
 * As of Freeplane v1.11.6, the original is buggy.
 * Extended
 * - to get canonical file, so that deduplication can take place
 * - to include schema `freeplane`, so that mind maps are also recognized as files.
 */
static File getCanonicalFile(URI uri, File mapFile) {
    try {
        if (uri == null)
            return null
        File file
        if (uri.scheme == 'file' || uri.scheme == null || uri.scheme == 'freeplane') {
            def uriPathForFile = uri.scheme == 'freeplane' ? uri.path.replaceFirst($/^/ /$, '') : uri.path
            if (SystemUtils.IS_OS_WINDOWS && uriPathForFile.startsWith('../')) {
                // Freeplane allows relative URI paths across drives, but such URI paths aren't recognized by File
                // File needs a path to start with a drive letter, therefore remove all `../` in front of a drive letter
                uriPathForFile = uriPathForFile.replaceFirst($/^(../)+(?=[a-zA-Z]:/)/$, '')
            }
            def fileFromUriPath = new File(uriPathForFile)
            if (fileFromUriPath.isAbsolute()) {
                file = fileFromUriPath
            } else {
                if (!mapFile)
                    return null
                else
                    file = new File(mapFile.parent, uriPathForFile)
            }
        } else {
            file = new File(uri)
        }
        return file.canonicalFile
    } catch (Exception ignored) {
        return null
    }
}

node = node as Node
c = c as Controller

def mapFile = node.mindMap.file
def files = c.selecteds.collect(new HashSet<File>()) { getCanonicalFile(it.link.uri, mapFile) }.findAll()
Toolkit.defaultToolkit.systemClipboard.setContents(new FileTransferable(files), new ClipboardOwner() {
    @Override
    void lostOwnership(Clipboard clipboard, Transferable transferable) {}
})
