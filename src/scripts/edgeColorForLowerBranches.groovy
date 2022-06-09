// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
// based on org.freeplane.features.edge.AutomaticEdgeColorHook.Listener.onNodeInserted

import org.freeplane.api.Node as FPN
import org.freeplane.features.edge.EdgeController
import org.freeplane.features.edge.mindmapmode.MEdgeController

final edgeColorForBranchesStyleName = 'Edge-color-for-branches level 0'

final mapModel = node.mindMap.delegate
final controller = EdgeController.controller as MEdgeController

c.find { it.style.name == edgeColorForBranchesStyleName }.each { FPN branchLevel0 ->
    branchLevel0.children.eachWithIndex { FPN branchLevel1, int i ->
        def colorCounter = i + 1
        def edgeColor = controller.getEdgeColor(mapModel, colorCounter)
        branchLevel1.findAll().each { it.style.edge.color = edgeColor }
    }
}
