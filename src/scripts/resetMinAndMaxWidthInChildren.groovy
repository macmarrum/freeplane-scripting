// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac3"})
import org.freeplane.api.NodeRO as FPN

node.children.each { FPN it ->
    if (it.visible) {
        it.style.maxNodeWidth = -1
        it.style.minNodeWidth = -1
    }
}
