// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})

final inboxStyleName = 'Inbox'
def customDateFormatForThisScriptOnly = 'yyyy-mm-dd,E'
def defaultDateFormatAsSetInPreferences = config.getProperty('date_format')
def defaultDateTimeFormatAsSetInPreferences = config.getProperty('datetime_format')
def now = new Date()
def inboxNode = c.findAll().find { it.style.name == inboxStyleName }
def newNode = inboxNode.createChild()
newNode.details = format(now, defaultDateTimeFormatAsSetInPreferences).toString()
c.select(newNode)
