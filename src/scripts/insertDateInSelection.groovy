// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
//import org.freeplane.features.format.FormatController

def customDateFormatForThisScriptOnly = 'yyyy-mm-dd,E'
//def defaultDateFormatAsSetInPreferences = FormatController.controller.defaultDateFormat.toPattern()
//def defaultDateTimeFormatAsSetInPreferences = FormatController.controller.defaultDateTimeFormat.toPattern()
def defaultDateFormatAsSetInPreferences = config.getProperty('date_format')
def defaultDateTimeFormatAsSetInPreferences = config.getProperty('datetime_format')
def now = new Date()
c.selecteds.each {
    it.text = format(now, defaultDateTimeFormatAsSetInPreferences)
}
