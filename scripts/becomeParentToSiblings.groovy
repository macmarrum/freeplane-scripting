// @ExecutionModes({ON_SINGLE_NODE})
if (node.isRoot()) return

def siblings = parent.children.findAll{it.id != node.id}
if (siblings.size() > 0) {
    //map.root.details = siblings.collect{"${it.id}: ${it.shortText}"}.join('\n')
    def positions = siblings.collect{ parent.getChildPosition(it) }
    //map.root.details = positions.collect{"${it}"}.join(',')
    siblings.eachWithIndex{ it, idx -> it.moveTo(node, positions[idx]) }
}
