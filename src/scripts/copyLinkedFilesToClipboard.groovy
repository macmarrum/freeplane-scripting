// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Copy"})
// https://github.com/freeplane/freeplane/issues/1376

import org.freeplane.plugin.script.proxy.ScriptUtils

import java.awt.*
import java.awt.datatransfer.*
import java.util.List

class FileTransferable implements Transferable {
    private static CRLF = '\r\n'
    private static uriListFlavor = new DataFlavor('text/uri-list;class=java.lang.String')
    private List<File> listOfFiles

    FileTransferable(List listOfFiles) {
        this.listOfFiles = listOfFiles
    }

    @Override
    DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DataFlavor.javaFileListFlavor, uriListFlavor}
    }

    @Override
    boolean isDataFlavorSupported(DataFlavor flavor) {
        return DataFlavor.javaFileListFlavor == flavor || uriListFlavor == flavor
    }

    @Override
    Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return switch (flavor) {
            case DataFlavor.javaFileListFlavor -> listOfFiles
            case uriListFlavor -> listOfFiles.collect { it.toURI() }.join(CRLF)
            default -> throw new UnsupportedFlavorException(flavor)
        }
    }
}

def listOfFiles = ScriptUtils.c().selecteds.collect { it.link.file }.findAll()
Toolkit.defaultToolkit.systemClipboard.setContents(new FileTransferable(listOfFiles), new ClipboardOwner() {
    @Override
    void lostOwnership(Clipboard clipboard, Transferable transferable) {}
})
