// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
c.selecteds.each { node -> node.cloud.enabled = !node.cloud.enabled }
