// @ExecutionModes({ON_SELECTED_NODE="/menu_bar/Mac1"})
// based on OpenCurrentMapDirAction.java
import org.freeplane.core.util.Hyperlink
import org.freeplane.features.link.LinkController

if (link.file)
    LinkController.getController().loadHyperlink(new Hyperlink(link.file.parentFile.toURI()))
else
    c.statusInfo = 'the node is missing a link of type "file"'
