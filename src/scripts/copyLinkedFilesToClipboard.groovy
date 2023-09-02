// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Copy"})
// https://github.com/freeplane/freeplane/issues/1376

import org.freeplane.plugin.script.proxy.ScriptUtils

import java.awt.*
import java.awt.datatransfer.*
import java.util.List

class FileTransferable implements Transferable {
    private static CRLF = '\r\n'
    private static LF = '\n'
    private static uriListFlavor = new DataFlavor('text/uri-list;class=java.lang.String')
    private List<File> files

    FileTransferable(List<File> files) {
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
            case DataFlavor.javaFileListFlavor -> files
            case uriListFlavor -> files.collect { it.toURI() }.join(CRLF) // rfc2483
            case DataFlavor.stringFlavor -> files.collect { it.path }.join(LF)
            default -> throw new UnsupportedFlavorException(flavor)
        }
    }
}

/**
 * A modified version of org.freeplane.plugin.script.proxy.LinkProxy#getFile()
 * As of Freeplane v1.11.6, the original is buggy.
 */
static File getFile(URI uri, File mapFile) {
    try {
        if (uri == null)
            return null
        if (uri.scheme == null || uri.scheme == 'file') {
            def file = new File(uri.path)
            if (file.isAbsolute()) {
                return file
            } else {
                return mapFile ? new File(mapFile.parent, uri.path) : null
            }
        } else {
            return new File(uri)
        }
    }
    catch (Exception e) {
        return null
    }
}

def mapFile = ScriptUtils.node().mindMap.file
def files = ScriptUtils.c().selecteds.collect { getFile(it.link.uri, mapFile) }.findAll()
Toolkit.defaultToolkit.systemClipboard.setContents(new FileTransferable(files), new ClipboardOwner() {
    @Override
    void lostOwnership(Clipboard clipboard, Transferable transferable) {}
})
