// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/GTD"})
def gtdStyle = 'Next Action'
def fallbackStyle = '!NextAction'
def styleName = X.getUserDefStyleIfExistsOrFallback(gtdStyle, fallbackStyle)
c.selecteds.each { X.setStyleAndTimestampInAttribute(styleName, it) }
