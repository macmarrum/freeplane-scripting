// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/GTD"})
def gtdStyle = 'Next Action Done'
def fallbackStyle = '!NextAction.Closed'
def styleName = X.getUserDefStyleIfExistsOrFallback(gtdStyle, fallbackStyle)
c.selecteds.each { X.setStyleAndTimestampInAttribute(styleName, it) }
