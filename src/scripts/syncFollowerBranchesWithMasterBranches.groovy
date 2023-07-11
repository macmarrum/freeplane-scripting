// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
// https://github.com/freeplane/freeplane/discussions/1297
package io.github.macmarrum.freeplane

import org.freeplane.api.MindMap
import org.freeplane.api.Node
import org.freeplane.core.util.LogUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

FlexibleBranchSyncer.sync(ScriptUtils.node().mindMap)

/**
 * The script supports the concept of flexible branch clones, as contrasted with full branch clones (node and subtree).
 * In each follower branch, the script creates single-node clones for each node in the master branch.
 * A follower branch is such whose root node has the attribute 'master.root' pointing to the root of the master branch,
 * e.g. ID_123456 or #ID_123456 or a hyperlink to the node.
 * A follower branch can follow more than one master branch, by having more 'master.root' attributes.
 * The script supports the addition of new nodes on the master branch(es) and the relocation of existing nodes.
 * Follower branches can add their stand-alone nodes, which will be kept intact.
 * The deletion of nodes on the master branch needs to be done in sync with the follower branches, e.g.
 * 1. Select the node
 * 2. In the Filter toolbar, choose "Clones of selection", then `Filter->Select all matching nodes`
 * 3. Delete
 * If shouldSetRoleAttributes is true, each node on the master branch is given the attribute 'role: follower/master',
 * so that it can be easily identified (e.g. in conditional styles), and the root of master branch is given the attribute 'role: master.root'.
 */
class FlexibleBranchSyncer {
    public static masterRoot = 'master.root'
    public static shouldSetRoleAttributes = false
    public static role = 'role'
    public static followerMaster = 'follower/master'

    static sync(MindMap mindMap) {
        mindMap.root.findAll().each { n ->
            n.attributes.getAll(masterRoot).each { masterBranchValue ->
                def followerBranchRoot = n
                def masterBranchRootId = masterBranchValue.toString()
                if (masterBranchRootId.startsWith('#ID')) // from Hyperlink
                    masterBranchRootId = masterBranchRootId.drop(1)
                def masterBranchRoot = mindMap.node(masterBranchRootId)
                if (masterBranchRoot) {
                    if (shouldSetRoleAttributes) masterBranchRoot[role] = masterRoot
                    syncFollowerWithMasterRecursively(followerBranchRoot, followerBranchRoot, masterBranchRoot)
                } else {
                    def message = "syncFollowersWIthTemplates - master ${masterBranchRootId} doesn't exist (any more)"
                    ScriptUtils.c().statusInfo = message
                    LogUtils.info(message)
                }
            }
        }
    }

    static syncFollowerWithMasterRecursively(Node followerBranchRoot, Node follower, Node master) {
        Node followerChildAsClone
        master.children.eachWithIndex { masterChild, masterChildPosition ->
            if (shouldSetRoleAttributes) masterChild[role] = followerMaster
            def masterChildClones = masterChild.nodesSharingContent
            followerChildAsClone = followerBranchRoot.findAll().find { it in masterChildClones }
            if (followerChildAsClone) { // found it
                if (followerChildAsClone.parent != follower)  // wrong location
                    followerChildAsClone.moveTo(follower, masterChildPosition)
            } else { // not found - append a clone
                followerChildAsClone = follower.appendAsCloneWithoutSubtree(masterChild)
                followerChildAsClone.moveTo(follower, masterChildPosition)
            }
            syncFollowerWithMasterRecursively(followerBranchRoot, followerChildAsClone, masterChild)
        }
    }
}
