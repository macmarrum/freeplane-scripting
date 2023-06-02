// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Insert"})

def numberOfNodesToInsert = 5
def levelToDefaultText = [
        1: '*1*',
        2: '*2*',
        3: '*3*',
        4: '*4*',
        5: '*5*',
]
def countHidden = false

def n = node
(0..<numberOfNodesToInsert).each {
    n = n.createChild()
    n.text = levelToDefaultText[n.getNodeLevel(countHidden)]
}
c.select(n)
