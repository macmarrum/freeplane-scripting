// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac3"})
import org.freeplane.api.NodeRO as FPN


int getWidth(FPN node, float zoom) {
    def v = node.delegate.viewers
    def viewCount = v.size()
    assert viewCount == 1, "Only one view is supported, got ${viewCount}"
    double width = v[0].mainView.width / zoom
    return Math.ceil(width)
}

float zoom = c.getZoom()
int maxWidthInSelection = 0
c.selecteds.each { FPN it ->
    int w = getWidth(it, zoom)
    if (w > maxWidthInSelection)
        maxWidthInSelection = w
}

c.selecteds.each { FPN it ->
    it.style.maxNodeWidth = maxWidthInSelection
    it.style.minNodeWidth = maxWidthInSelection
}
