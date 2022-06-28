import org.freeplane.features.styles.MapStyleModel

// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})

final journalRootStyleName = 'Journal root'
final journalNodeStyleName = 'Journal'
def customDateFormatForThisScriptOnly = 'yyyy-mm-dd,E'
def defaultDateFormatAsSetInPreferences = config.getProperty('date_format')
def defaultDateTimeFormatAsSetInPreferences = config.getProperty('datetime_format')
def now = new Date()
def journalRoot =  c.findAll().find { it.style.name == journalRootStyleName } ?: node.mindMap.root
def journalDate = format(now, defaultDateFormatAsSetInPreferences)
def journalNode = journalRoot.findAll().find { it.text == journalDate.toString() } ?: journalRoot.createChild(journalDate)
if (MapStyleModel.getExtension(node.delegate.map).styles.find { it.toString() == journalNodeStyleName })
    journalNode.style.name = journalNodeStyleName
c.select(journalNode)
