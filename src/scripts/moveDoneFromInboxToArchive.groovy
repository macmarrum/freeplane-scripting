// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})

def inboxStyleName = 'Inbox'
def archiveStyleName = 'Archive'
def doneStyleName = 'Done'

def allNodes = c.findAll()
def inboxNode = allNodes.find { it.style.name == inboxStyleName }
def archiveNode = allNodes.find { it.style.name == archiveStyleName }

inboxNode.find { it.style.name == doneStyleName }.each { it.moveTo(archiveNode) }
