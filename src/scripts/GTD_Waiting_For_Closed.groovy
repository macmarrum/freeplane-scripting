// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
c.selecteds.each { X.setStyleAndTimestampInAttribute('!WaitingFor.Closed', it) }
