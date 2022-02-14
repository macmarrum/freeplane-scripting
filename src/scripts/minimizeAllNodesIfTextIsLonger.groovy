// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac3"})

def max_shortened_text_length = config.getIntProperty("max_shortened_text_length")
node.mindMap.root.findAll().each {
    it.setMinimized(it.to.plain.size() > max_shortened_text_length)
}
