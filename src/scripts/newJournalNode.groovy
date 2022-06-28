// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})

final journalRootStyleName = 'Journal root'
def customDateFormatForThisScriptOnly = 'yyyy-mm-dd,E'
def defaultDateFormatAsSetInPreferences = config.getProperty('date_format')
def defaultDateTimeFormatAsSetInPreferences = config.getProperty('datetime_format')
def now = new Date()
def journalRoot =  c.findAll().find { it.style.name == journalRootStyleName } ?: node.mindMap.root
def journalDate = format(now, defaultDateFormatAsSetInPreferences)
def journalNode = journalRoot.findAll().find { it.text == journalDate.toString() } ?: journalRoot.createChild(journalDate)
c.select(journalNode)
