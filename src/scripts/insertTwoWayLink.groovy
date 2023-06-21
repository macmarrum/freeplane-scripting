// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Insert"})
// https://github.com/freeplane/freeplane/discussions/1255

def nodes = c.selecteds.collect()
if (nodes.size() != 2)
    c.statusInfo = "got ${nodes.size()} nodes - expected 2"
else {
    nodes[0].link.node = nodes[1]
    nodes[1].link.node = nodes[0]
}
