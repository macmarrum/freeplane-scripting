// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Insert"})
// https://github.com/freeplane/freeplane/discussions/1255

def nodes = c.selecteds.collect()
if (nodes.size() == 1) {
    def oldTarget = node.link.node
    menuUtils.executeMenuItems(['MakeLinkToAnchorAction'])
    def target = node.link.node
    if (target && target != oldTarget)
        target.link.node = node
    else
        c.statusInfo = "Cannot link. Was an anchor set?"
} else {
    c.statusInfo = "Cannot link. Got ${nodes.size()} nodes - expected 1"
}
