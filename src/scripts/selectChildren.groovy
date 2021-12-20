// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})
// for each selected node
//  find its visible children (list)
//   add the list to list-of-lists
//    flatten the list-of-lists to a single list
//     select nodes from the single list
c.select(c.selecteds.collect{it.children.findAll{it.visible}}.flatten())
