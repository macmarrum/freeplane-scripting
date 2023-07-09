// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
// https://github.com/freeplane/freeplane/discussions/1297


import org.freeplane.api.Node
import org.freeplane.core.util.LogUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

def mindMap = ScriptUtils.node().mindMap
final TEMPLATE = 'template'

mindMap.root.findAll().each { n ->
    println(">> testing node ${n.id}")
    n.attributes.getAll(TEMPLATE).each { templateAsValue ->
        def follower = n
        def templateId = templateAsValue.toString()
        if (templateId.startsWith('#ID')) // from Hyperlink
            templateId = templateId.drop(1)
        def template = mindMap.node(templateId)
        if (template)
            syncFollowerWithTemplateRecursively(follower, follower, template)
        else
            LogUtils.info("syncFollowersWIthTemplates - template ${templateId} doesn't exist (any more)")
    }
}

static syncFollowerWithTemplateRecursively(Node followerRoot, Node follower, Node template) {
    Node followerChildAsClone
    template.children.eachWithIndex { templateChild, templateChildPosition ->
        def templateChildClones = templateChild.nodesSharingContent
        followerChildAsClone = followerRoot.findAll().find { it in templateChildClones }
        if (followerChildAsClone) { // found it
            if (followerChildAsClone.parent != follower)  // wrong location
                followerChildAsClone.moveTo(follower, templateChildPosition)
        } else { // not found - append a clone
            followerChildAsClone = follower.appendAsCloneWithoutSubtree(templateChild)
            followerChildAsClone.moveTo(follower, templateChildPosition)
        }
        syncFollowerWithTemplateRecursively(followerRoot, followerChildAsClone, templateChild)
    }
}
