// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/GTD"})
def gtdStyle = 'Waiting For'
def fallbackStyle = '!WaitingFor'
def styleName = X.getUserDefStyleIfExistsOrFallback(gtdStyle, fallbackStyle)
c.selecteds.each { X.setStyleAndTimestampInAttribute(styleName, it) }
