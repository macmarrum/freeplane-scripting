// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Copy"})
// https://github.com/freeplane/freeplane/issues/1376
import org.freeplane.api.Node
import org.freeplane.plugin.script.proxy.ScriptUtils

import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.ClipboardOwner
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException

class FileTransferable implements Transferable {
    private static CRLF = '\r\n'
    private static DataFlavor uriListFlavor = new DataFlavor('text/uri-list;class=java.lang.String')
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

def listOfFiles = ScriptUtils.c().selecteds.collect { Node it -> it.link.file }.findAll()
Toolkit.defaultToolkit.systemClipboard.setContents(new FileTransferable(listOfFiles), new ClipboardOwner() {
    @Override
    void lostOwnership(Clipboard clipboard, Transferable transferable) {}
})
