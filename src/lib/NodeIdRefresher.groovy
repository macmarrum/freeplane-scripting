import groovy.json.JsonSlurper
import org.freeplane.api.Node as FN

class NodeIdRefresher {
    public static final ATTR_PREFIX = 'freshIdToOriginal_'

    static refreshAll(FN node) {
        def mm = node.mindMap
        def root = mm.root
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
        def sb = new StringBuilder('{\n')
        nodeIdToFreshId.each { k, v -> sb << "\"$v\": \"$k\",\n" }
        sb << '}'

        def now = (new Date()).format('yyyy-MM-dd_HH:mm:ss')
        root["${ATTR_PREFIX}${now}"] = sb
    }

    static Map.Entry<String, Object> getLatestSavepoint(FN root) {
        def savepoints = root.attributes.findAll { it.key.startsWith(ATTR_PREFIX) }
        if (savepoints)
            return savepoints.sort().last()
    }

    static restoreAll(FN node) {
        def root = node.mindMap.root
        def savepoint = getLatestSavepoint(root)
        if (savepoint) {
            def hashmapAsJson = savepoint.value as String
            def freshIdToOriginal = new JsonSlurper().parseText(hashmapAsJson)
            String originalId
            root.findAll().each {
                originalId = freshIdToOriginal.get(it.id)
                if (originalId)
                    it.delegate.setID(originalId)
            }
            root['restored_' + savepoint.key] = savepoint.value
            root.attributes.removeAll(savepoint.key)
        }
    }
}
