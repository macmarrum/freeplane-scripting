// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})

// put the name of your custom style, if different
def jumpInStyleName = 'JumpIn'

def styleName = node.style.name
if (!styleName) {
    // no style -- apply jumpInStyleName
    node.style.name = jumpInStyleName
} else if (styleName == jumpInStyleName) {
    // jumpInStyleName is already applied
} else {
    // there is already a style applied, and it isn't jumpInStyleName
    // save the currently applied style as a Node Conditional Style, if not already there
    def styleAlreadyAdded = node.conditionalStyles.collect().find { it.styleName == styleName }
    if (!styleAlreadyAdded)
        node.conditionalStyles.add(true, null, styleName, false)
    // apply jumpInStyleName
    node.style.name = jumpInStyleName
}
menuUtils.executeMenuItems(['JumpInAction'])
