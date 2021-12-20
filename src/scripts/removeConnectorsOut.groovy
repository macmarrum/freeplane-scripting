// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
c.selecteds.each { n ->
    n.connectorsOut.each { n.removeConnector(it) }
}