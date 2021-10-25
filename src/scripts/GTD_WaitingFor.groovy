// @ExecutionModes({ON_SINGLE_NODE})
c.selecteds.each { X.setStyleAndTimestampInAttribute('!WaitingFor', it) }