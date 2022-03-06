// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Move"})
def selecteds = c.selecteds.collect()
selecteds.each {
    c.select(it)
    menuUtils.executeMenuItems(['ChangeNodeLevelRightsAction'])
}
c.select(selecteds)
