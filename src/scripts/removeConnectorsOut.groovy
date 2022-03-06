// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Remove"})
c.selecteds.each { n ->
    n.connectorsOut.each { n.removeConnector(it) }
}