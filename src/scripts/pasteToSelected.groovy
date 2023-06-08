// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Paste"})
def selecteds = c.selecteds.collect()
selecteds.each {
    c.select(it)
    menuUtils.executeMenuItems(['PasteAction'])
}
c.select(selecteds)
