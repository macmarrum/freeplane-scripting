// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Insert"})

def numberOfNodesToInsert = 5
def defaultText = '*default*'

def n = node
(0..<numberOfNodesToInsert).each { n = n.createChild(defaultText) }
c.select(n)
