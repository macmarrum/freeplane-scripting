// @ExecutionModes({ON_SINGLE_NODE})
c.selecteds.each{
	it.pasteAsClone()
	it.folded = false
}
