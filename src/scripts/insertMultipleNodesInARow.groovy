// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Insert"})

def numberOfNodesToInsert = 5

def n = node
(0..<numberOfNodesToInsert).each { n = n.createChild() }
c.select(n)
