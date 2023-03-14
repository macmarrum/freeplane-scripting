import org.freeplane.api.Node as FN

class NodeIdRefresher {

    static refresh(FN root) {
        def mm = root.mindMap
        def nodeIdToFreshId = new LinkedHashMap<String, String>()
        String nodeId
        String freshId
        root.findAll().each {
            nodeId = it.id
            freshId = nodeIdToFreshId.get(nodeId)
            if (freshId === null) { // not yet re-generated
                freshId = mm.delegate.generateNodeID(null) // it makes sure the generated ID is unique in the map
                // guard against using an ID which was used in the original node set, but is now re-generated, so no longer part of the map
                while (nodeIdToFreshId.containsKey(freshId))
                    freshId = mm.delegate.generateNodeID(null)
                nodeIdToFreshId[nodeId] = freshId
            }
            it.delegate.setID(freshId)
        }
        def sb = new StringBuilder('[\n')
        nodeIdToFreshId.each { k, v -> sb << "$v: '$k',\n" }
        sb << ']'

        def now = (new Date()).format('yyyy-MM-dd_HH:MM:SS')
        root["freshIdToOriginal_${now}"] = sb
    }
}
