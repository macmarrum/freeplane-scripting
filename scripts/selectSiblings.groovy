// @ExecutionModes({ON_SINGLE_NODE})
// for each selected node
//  find its parent's visible children except for itself (list)
//   add the list to list-of-lists
//    flatten the list-of-lists to a single list
//     select nodes from the single list
c.select(c.selecteds.collect{self -> self.parent.children.findAll{it.visible && it.id != self.id}}.flatten())
