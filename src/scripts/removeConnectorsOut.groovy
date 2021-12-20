// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
c.selecteds.each { n ->
    n.connectorsOut.each { n.removeConnector(it) }
}