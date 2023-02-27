// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
import org.freeplane.plugin.script.proxy.ScriptUtils

def node = ScriptUtils.node()
def mm = node.mindMap
def root = node.mindMap.root
def nodeIdToRegenId = new LinkedHashMap<String, String>()
String nodeId
String regeneratedId
root.findAll().each {
    nodeId = it.id
    regeneratedId = nodeIdToRegenId.get(nodeId)
    if (regeneratedId.is(null)) { // not yet re-generated
        regeneratedId = mm.delegate.generateNodeID(null) // it makes sure the generated ID is unique in the map
        // guard against using an ID which was used in the original node set, but is now re-generated, so no longer part of the map
        while (nodeIdToRegenId.containsKey(regeneratedId))
            regeneratedId = mm.delegate.generateNodeID(null)
        nodeIdToRegenId[nodeId] = regeneratedId
    }
    it.delegate.setID(regeneratedId)
}
def sb = new StringBuilder('[\n')
nodeIdToRegenId.each { k, v -> sb << "$k: '$v',\n" }
sb << ']'

def now = java.time.Instant.now().toString()[0..18].replace('T', '_')
root["nodeIdToRegenId_${now}"] = sb
