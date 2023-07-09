// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
// https://github.com/freeplane/freeplane/discussions/1297

import org.freeplane.api.Node
import org.freeplane.plugin.script.proxy.ScriptUtils

def node = ScriptUtils.node()
final root = node.mindMap.root
final TEMPLATE = 'template'

root.findAll().each { n ->
    println(">> testing node ${n.id}")
    n.attributes.getAll(TEMPLATE).each { templateId ->
        def follower = n
        def template = n.mindMap.node(templateId.toString())
        syncFollowerWithTemplateRecursively(follower, template)
    }
}

static syncFollowerWithTemplateRecursively(Node follower, Node template) {
    def followerChildren = follower.children.collect()
    Node followerChildAsClone
    template.children.eachWithIndex { templateChild, templateChildPosition ->
        def templateChildClones = templateChild.nodesSharingContent
        if (!followerChildren) {
            followerChildAsClone = follower.appendAsCloneWithoutSubtree(templateChild)
        } else {
            followerChildAsClone = followerChildren.find { followerChild -> followerChild in templateChildClones }
            if (!followerChildAsClone) {
                followerChildAsClone = follower.appendAsCloneWithoutSubtree(templateChild)
            }
        }
        syncFollowerWithTemplateRecursively(followerChildAsClone, templateChild)
    }
}
