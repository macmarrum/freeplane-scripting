/**
 * Copyright (C) 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Select"})

import org.freeplane.core.util.MenuUtils
import org.freeplane.features.map.NodeIterator
import org.freeplane.features.map.NodeModel
import org.freeplane.features.mode.Controller

def nodeModel = node.delegate as NodeModel
def clones = nodeModel.allClones()
if (clones.size() > 1) { // => clones instanceof MultipleNodeList
    def clonesInMindMapOrder = new ArrayList<NodeModel>(clones.size())
    NodeIterator.of(nodeModel.map.rootNode, NodeModel::getChildren).forEachRemaining {
        if (it in clones.toCollection()) // MultipleNodeList::toCollection() => LinkedList<NodeModel> nodes
            clonesInMindMapOrder << it
    }
    def currentIndex = clonesInMindMapOrder.indexOf(nodeModel)
    def nextIndex = currentIndex < clonesInMindMapOrder.size() - 1 ? ++currentIndex : 0
    def nodeModelToSelect = clonesInMindMapOrder[nextIndex]
    Controller.currentModeController.mapController.displayNode(nodeModelToSelect)
    Controller.currentController.selection.selectAsTheOnlyOneSelected(nodeModelToSelect)
} else {
    MenuUtils.executeMenuItems(['FollowLinkAction'])
}
