import org.freeplane.api.LengthUnit
import org.freeplane.api.Node as FN
import org.freeplane.api.Quantity
import org.freeplane.features.map.MapModel
import org.freeplane.features.map.NodeModel
import org.freeplane.features.map.SummaryNode
import org.freeplane.features.nodelocation.LocationModel
import org.freeplane.features.styles.ConditionalStyleModel
import org.freeplane.features.styles.LogicalStyleController
import org.freeplane.features.styles.StyleFactory
import org.freeplane.features.styles.mindmapmode.MLogicalStyleController

class CompactLeaves {
    static final LEAF_CHILDREN_STYLE_NAME = 'Leaf children'
    static final VERTICAL_SHIFT = Quantity.fromString('-17', LengthUnit.pt)
    static final VERTICAL_SHIFT_NONE = LocationModel.NULL_LOCATION.shiftY

    static debug(String... args) {
        ''
//        println(args.join(' '))
    }

    static forBranch(FN root) {
        def m = createNodeToVisibleNonFreeChildren(root)
        m.each { entry ->
            def node = entry.key
            def children = entry.value
            if (children.size() > 0) {
                if (!node.folded) {
                    if (children.every { isDisplayLeaf(it, m[it]) }) {
                        addNodeConditionalStyleIfNotPresent(node, LEAF_CHILDREN_STYLE_NAME)
                        children.each {
                            debug(":1: ${it.id} ${it.text} -> ${VERTICAL_SHIFT_NONE}")
                            if (it.visible) // ignore summary nodes
                                it.verticalShift = VERTICAL_SHIFT_NONE
                        }
                    } else {
                        removeNodeConditionalStyleIfPresent(node, LEAF_CHILDREN_STYLE_NAME)
                        children.eachWithIndex { FN child, int i ->
                            if (i > 0 && child.visible) {  // ignore summary nodes
                                def prevChild = getPreviousNonSummaryNode(i, children)
                                if (prevChild) {
                                    if (isDisplayLeaf(child, m[child]) && isDisplayLeaf(prevChild, m[prevChild])) {
                                        debug(":2: ${child.id} ${child.text} -> ${VERTICAL_SHIFT}")
                                        child.verticalShift = VERTICAL_SHIFT
                                    } else if (child.verticalShift != VERTICAL_SHIFT_NONE) {
                                        debug(":2: ${child.id} ${child.text} -> ${VERTICAL_SHIFT_NONE}")
                                        child.verticalShift = VERTICAL_SHIFT_NONE
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    static isDisplayLeaf(FN node, List<FN> children) {
        return children.size() == 0 || node.folded || SummaryNode.isSummaryNode(node.delegate)
    }

    static getPreviousNonSummaryNode(int index, List<FN> children) {
        def i = index - 1
        while (i >= 0) {
            if (children[i].visible) // ignore summary nodes
                return children[i]
        }
    }

    static Map<FN, List<FN>> createNodeToVisibleNonFreeChildren(FN node, Map<FN, List<FN>> m = null) {
        if (m === null)
            m = new HashMap<FN, List<FN>>()
        m[node] = node.children.findAll { FN child -> (child.visible || SummaryNode.isSummaryNode(child.delegate)) && !child.free }
        m[node].each { FN child ->
            createNodeToVisibleNonFreeChildren(child, m)
        }
        return m
    }

    static addNodeConditionalStyleIfNotPresent(FN node, String styleName) {
        NodeModel nodeModel = node.delegate
        MapModel mapModel = nodeModel.map
        def condiStyleModel = getOrCreateConditionalStyleModelOf(nodeModel)
        def iStyle = StyleFactory.create(styleName)
        if (!condiStyleModel.styles.any { it.style == iStyle }) {
            def logicalStyleController = LogicalStyleController.controller as MLogicalStyleController
            logicalStyleController.addConditionalStyle(mapModel, condiStyleModel, true, null, iStyle, false)
        }
    }

    static ConditionalStyleModel getOrCreateConditionalStyleModelOf(NodeModel node) {
        def conditionalStyleModel = node.getExtension(ConditionalStyleModel.class) as ConditionalStyleModel
        if (conditionalStyleModel == null) {
            conditionalStyleModel = new ConditionalStyleModel()
            node.addExtension(conditionalStyleModel)
        }
        return conditionalStyleModel
    }

    static removeNodeConditionalStyleIfPresent(FN node, String styleName) {
        NodeModel nodeModel = node.delegate
        MapModel mapModel = nodeModel.map
        def condiStyleModel = nodeModel.getExtension(ConditionalStyleModel.class) as ConditionalStyleModel
        if (condiStyleModel !== null) {
            def logicalStyleController = LogicalStyleController.controller as MLogicalStyleController
            def iStyle = StyleFactory.create(styleName)
            condiStyleModel.styles.eachWithIndex { it, i ->
                if (it.style == iStyle)
                    logicalStyleController.removeConditionalStyle(mapModel, condiStyleModel, i)
            }
        }
    }
}
