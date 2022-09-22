import org.freeplane.api.LengthUnit
import org.freeplane.api.Node as FN
import org.freeplane.api.NodeChangeListener
import org.freeplane.api.NodeChanged
import org.freeplane.api.Quantity
import org.freeplane.core.extension.IExtension
import org.freeplane.features.edge.EdgeController
import org.freeplane.features.edge.mindmapmode.MEdgeController
import org.freeplane.features.filter.FilterController
import org.freeplane.features.map.*
import org.freeplane.features.mode.Controller
import org.freeplane.features.nodelocation.LocationModel
import org.freeplane.features.nodestyle.NodeBorderModel
import org.freeplane.features.styles.ConditionalStyleModel
import org.freeplane.features.styles.LogicalStyleController
import org.freeplane.features.styles.StyleFactory
import org.freeplane.features.styles.mindmapmode.MLogicalStyleController
import org.freeplane.plugin.script.FreeplaneScriptBaseClass

import java.awt.*
import java.text.SimpleDateFormat
import java.util.List

class MacmarrumChangeListenerUtils {
    static dfLong = new SimpleDateFormat('yyyy-MM-dd,E HH:mm:ss')

    /*
     * Note: config is read only at start-up. If changed afterwards, the Listener needs to be restarted.
     */

    static debug(String... args) {
        println(args.collect { it.replaceAll('\n', ' | ') }.join(' '))
    }

    static int toggleChangeListeners(FN root) {
        int n = toggleNodeChangeListener(root)
        int m = toggleMapChangeListener(root)
//        updateInformationNode(root, n + m)
        return n + m
    }

    static int toggleNodeChangeListener(FN root) {
        def macmarrumListeners = root.mindMap.listeners.findAll {
            it.class.simpleName == MacmarrumNodeChangeListener.class.simpleName
        }
        if (macmarrumListeners.size() == 0) {
            root.mindMap.addListener(new MacmarrumNodeChangeListener())
            debug(":: toggleNodeChangeListener => ON")
            return 1
        } else {
            macmarrumListeners.each {
                root.mindMap.removeListener(it)
            }
            debug(":: toggleNodeChangeListener => OFF")
            return 0
        }
    }

    static int toggleMapChangeListener(FN root) {
        def mapController = Controller.currentModeController.mapController
        def macmarrumListeners = mapController.mapChangeListeners.findAll {
            it.class.simpleName == MacmarrumMapChangeListener.class.simpleName
        }
        NodeModel rootModel = root.delegate
        def macmarrumMapChangeListenerEnablerForMap = MacmarrumMapChangeListenerEnablerForMap.getExtensionOf(rootModel)
        if (macmarrumMapChangeListenerEnablerForMap === null) {
            rootModel.addExtension(new MacmarrumMapChangeListenerEnablerForMap(root))
            debug(":: toggleMapChangeListener for ${root.mindMap.file.name} => ON")
            Utils.applyEdgeColorsToBranchesAndAlteringColorsToLeafsInMap(root)
            Utils.setHorizontalShiftForBranch(root)
            CompactLeaves.forBranch(root)
            enableMapChangeListenerIfNotYetEnabled(macmarrumListeners, mapController)
            return 1
        } else {
            def extensions = rootModel.sharedExtensions.values()
            extensions.collect().each {
                if (it.class.name == MacmarrumMapChangeListenerEnablerForMap.class.name && it.root == root) {
                    extensions.remove(it)
                    debug(":: toggleMapChangeListener for ${root.mindMap.file.name} => OFF")
                }
            }
            return 0
        }
    }

    static boolean enableMapChangeListenerIfNotYetEnabled(Collection<IMapChangeListener> macmarrumListeners, MapController mapController) {
        if (macmarrumListeners.size() == 0) {
            mapController.addMapChangeListener(new MacmarrumMapChangeListener())
            debug(":: enableMapChangeListenerIfNotYetEnabled => ON")
            return true
        } else {
            debug(":: enableMapChangeListenerIfNotYetEnabled => already enabled")
            return false
        }
    }

    static disableMapChangeListener(Collection<IMapChangeListener> macmarrumListeners, MapController mapController) {
        macmarrumListeners.each {
            mapController.removeMapChangeListener(it)
        }
    }

    static updateInformationNode(FN root, int listnersCount) {
        String alias = 'macmarrumChangeListeners'
        List<FN> foundlings = root.find { FN it -> it.isGlobal && it.alias == alias }
        FN target = foundlings.size() == 1 ? foundlings[0] : null
        if (!target) {
            target = root.createChild(alias.replaceAll(/_/, ' '))
            target.sideAtRoot = 'LEFT'
            target.isGlobal = true
            target.alias = alias
        }
        String now = dfLong.format(new Date())
        String triangular_flag = 'emoji-1F6A9'

        if (listnersCount == 0) {
            target['off'] = now
            while (triangular_flag in target.icons.icons) {
                target.icons.remove(triangular_flag)
            }
        } else {
            target['on'] = now
            if (triangular_flag !in target.icons.icons)
                target.icons.add(triangular_flag)
        }
    }

    static enum Feature {
        MINI_NODES, BRANCH_COLORS, H_GAP, COMPACT_LEAVES
    }

    static final FEATURES_ATTR_NAME = 'MacmarrumFeatures'

    static isEnabled(Feature feature, FN root) {
        return root[FEATURES_ATTR_NAME]?.text?.find("\\b${feature}\\b")
//            def features = root[FEATURES_ATTR_NAME]?.text?.findAll(/[^,\s]+/)
//            return feature.toString() in features
    }

    static class Utils {
        static String horizontalShift = '1 cm'
        static Quantity<LengthUnit> hGap = Quantity.fromString(horizontalShift, LengthUnit.px)
        static final controller = EdgeController.controller as MEdgeController

        static pullAndUpdateHGapIfNotNull(FN root) {
            def horizontalShift = root['h.shift']?.text
            if (horizontalShift != null) {
                this.horizontalShift = horizontalShift
                if (horizontalShift != 'none')
                    hGap = Quantity.fromString(horizontalShift, LengthUnit.px)
            }
        }

        static setHorizontalShift(FN node) {
//            return // DISABLED
            if (isEnabled(Feature.H_GAP, node.mindMap.root) && horizontalShift != 'none' && node.horizontalShiftAsLength != hGap && node.visible && !node.free) {
//            debug(">> setHorizontalShift")
                node.horizontalShift = hGap
            }
        }

        static setHorizontalShiftForBranch(FN root) {
            root.findAll().drop(1).each { FN it -> setHorizontalShift(it) }
        }

        static int getGrandchildVisiblePosition(FN leaf) {
            def parent = leaf.parent
            if (parent == null) // root node
                return 0
            int leafCount = 0
            def grandparent = parent?.parent
            if (grandparent) {
                def parentPosition = grandparent.getChildPosition(parent)
                if (parentPosition > 0) {
                    def grandparentChildren = grandparent.children
                    (0..<parentPosition).each { i ->
                        leafCount += grandparentChildren[i].children.findAll { it.visible }.size()
                    }
                }
            }
            for (FN child : parent.children) {
                if (child.id == leaf.id)
                    return leafCount
                if (child.visible)
                    leafCount++
            }
        }

        static applyEdgeColorsToBranchesAndAlteringColorsToLeafsInMap(FN root) {
            if (!isEnabled(Feature.BRANCH_COLORS, root))
                return
            int colorCounter
            Color edgeColor
            Color bgColor
            MapModel mapModel = root.mindMap.delegate
            root.children.eachWithIndex { level1, i ->
                colorCounter = i + 1
                edgeColor = controller.getEdgeColor(mapModel, colorCounter)
                level1.find { it.visible }.each { FN it ->
                    NodeModel nodeModel = it.delegate
                    def nodeBorderModel = NodeBorderModel.createNodeBorderModel(nodeModel)
                    if (it.leaf && it.getNodeLevel(false) > 1) {
                        bgColor = getGrandchildVisiblePosition(it) % 2 == 0 ? edgeColor.brighter() : edgeColor.darker()
                        nodeBorderModel.borderColorMatchesEdgeColor = false
                    } else {
                        bgColor = edgeColor
                        nodeBorderModel.borderColorMatchesEdgeColor = true
                    }
                    it.style.edge.color = edgeColor
                    nodeBorderModel.borderColor = bgColor
                    it.style.backgroundColor = bgColor
                }
            }
        }

        static final config = new FreeplaneScriptBaseClass.ConfigProperties()
        static final max_shortened_text_length = config.getIntProperty("max_shortened_text_length")

        static minimizeNodeIfTextIsLonger(FN node) {
            if (isEnabled(Feature.MINI_NODES, node.mindMap.root) && node.visible) {
                // use node.to to get the size of the resulting value, not the underlying formula
                // NB. node.to triggers formula evaluation if core is not IFormattedObject, Number or Date
                node.minimized = node.to.plain.size() > max_shortened_text_length
            }
        }

        static applyStyleBasedOnOtherNodesStyle(FN node) {
            node.mindMap.root.findAll().drop(1).each {
                if (it.visible) {
                    def newStyle = calculateStyle(it)
                    if (newStyle !== null && node.style.name != newStyle) {
                        node.style.name = newStyle
                    }
                }
            }
        }

        static String calculateStyle(FN node) {
            return null
        }
    }


    static class CompactLeaves {
        static final VERTICAL_SHIFT_SPACIOUS_ATTR_NAME = 'MacmarrumSpaciousVGap'
        static final VERTICAL_SHIFT_NONE = LocationModel.NULL_LOCATION.shiftY

        static debug(String... args) {
//            return // DISABLE
            println(args.join(' '))
        }

        static forBranch(FN root) {
            if (!isEnabled(Feature.COMPACT_LEAVES, root))
                return
            final VERTICAL_SHIFT_SPACIOUS = getVerticalShiftSpacious(root)
            def m = createNodeToVisibleNonFreeChildren(root)
            m.each { entry ->
                def node = entry.key
                def children = entry.value
                if (children.size() > 0) {
                    if (!node.folded) {
                        if (children.every { isDisplayLeaf(it, m[it]) }) {
                            children.each { FN child ->
                                if (child.visible) {// ignore summary nodes
                                    setVerticalShiftIfDifferent(child, VERTICAL_SHIFT_NONE, ':1:')
                                }
                            }
                        } else {
                            children.eachWithIndex { FN child, int i ->
                                if (child.visible) { // ignore summary nodes
                                    if (i == 0) {
                                        setVerticalShiftIfDifferent(child, VERTICAL_SHIFT_NONE, ':0:')
                                    } else {
                                        def prevChild = getPreviousVisible(i, children)
                                        if (prevChild) {
                                            if (isDisplayLeaf(child, m[child]) && isDisplayLeaf(prevChild, m[prevChild]))
                                                setVerticalShiftIfDifferent(child, VERTICAL_SHIFT_NONE, ':2:')
                                            else
                                                setVerticalShiftIfDifferent(child, VERTICAL_SHIFT_SPACIOUS, ':2:')
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    children.each { FN child ->
                        if (child.visible)
                            setVerticalShiftIfDifferent(child, VERTICAL_SHIFT_NONE, ':3:')
                    }
                }
            }
        }

        static getVerticalShiftSpacious(FN root) {
            def value = root[VERTICAL_SHIFT_SPACIOUS_ATTR_NAME]?.num as String ?: '10'
            return Quantity.fromString(value, LengthUnit.pt)
        }

        static isDisplayLeaf(FN node, List<FN> children) {
            return children.size() == 0 || node.folded || SummaryNode.isSummaryNode(node.delegate)
        }

        static getPreviousVisible(int index, List<FN> children) {
            def i = index - 1
            while (i >= 0) {
                if (children[i].visible) // ignore summary nodes
                    return children[i]
            }
        }

        static setVerticalShiftIfDifferent(FN child, Quantity<LengthUnit> verticalShift, String debugPrefix = '') {
            if (child.verticalShift != verticalShift) {
                def indicator = verticalShift == VERTICAL_SHIFT_NONE ? '||' : '<>'
                debug(debugPrefix, indicator, child.text, child.id, '->', verticalShift.toString())
                child.verticalShift = verticalShift
            }
        }

        static java.util.Map<FN, List<FN>> createNodeToVisibleNonFreeChildren(FN node, java.util.Map<FN, List<FN>> m = null) {
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


    static class MacmarrumMapChangeListenerEnablerForMap implements IExtension {
        public FN root

        MacmarrumMapChangeListenerEnablerForMap(FN root) {
            this.root = root
        }

        static IExtension getExtensionOf(NodeModel nodeModel) {
            def extensions = nodeModel.sharedExtensions.values()
//            debug(extensions.collect{it.class.simpleName})
            def ext = extensions.find { it.class.name == this.name }
//            debug("== MacmarrumMapChangeListenerEnablerForMap is ${ext ? 'not ' : ''}null")
            return ext
        }
    }


    static class MacmarrumMapChangeListener implements IMapChangeListener {

        static FN getRootIfListenerIsEnabled(MapModel mapModel) {
            return MacmarrumMapChangeListenerEnablerForMap.getExtensionOf(mapModel.rootNode)?.root
        }

        static boolean isRegularMap(FN root, NodeModel changedNode) {
            return root.id == changedNode.map.rootNode.id
        }

        static boolean isVisible(NodeModel node) {
            return node.hasVisibleContent(FilterController.getFilter(node.map))
        }

        void onNodeInserted(NodeModel parent, NodeModel child, int newIndex) {
            FN root = getRootIfListenerIsEnabled(child.map)
            if (root && isRegularMap(root, child)) {
                def id = child.createID() // gets id or generates it
//                debug("++ ${id}")
                def node = root.mindMap.node(id)
                MacmarrumNodeChangeListener.canReact = false
                Utils.setHorizontalShift(node)
                def parentNode = root.mindMap.node(parent.ID)
                CompactLeaves.forBranch(parentNode.parent ?: parentNode) // grandparent if exists, else parent - to cover `0 -> 1` children
                Utils.applyEdgeColorsToBranchesAndAlteringColorsToLeafsInMap(root)
                MacmarrumNodeChangeListener.canReact = true
            }
        }

        void onNodeMoved(NodeMoveEvent nodeMoveEvent) {
            FN root = getRootIfListenerIsEnabled(nodeMoveEvent.child.map)
            if (root && isRegularMap(root, nodeMoveEvent.child)) {
//                debug("-> ${nodeMoveEvent.child.id}")
                MacmarrumNodeChangeListener.canReact = false
                CompactLeaves.forBranch(root.mindMap.node(nodeMoveEvent.oldParent.ID))
                CompactLeaves.forBranch(root.mindMap.node(nodeMoveEvent.newParent.ID))
                Utils.applyEdgeColorsToBranchesAndAlteringColorsToLeafsInMap(root)
                MacmarrumNodeChangeListener.canReact = true
            }
        }

        void onNodeDeleted(NodeDeletionEvent nodeDeletionEvent) {
            FN root = getRootIfListenerIsEnabled(nodeDeletionEvent.node.map)
            if (root && isRegularMap(root, nodeDeletionEvent.node)) {
//                debug("-- ${nodeDeletionEvent.node.id}")
                MacmarrumNodeChangeListener.canReact = false
                def parent = root.mindMap.node(nodeDeletionEvent.parent.ID)
                CompactLeaves.forBranch(parent.parent ?: parent) // grandparent if exists, else parent - to cover `1 -> 0` children
                Utils.applyEdgeColorsToBranchesAndAlteringColorsToLeafsInMap(root)
                MacmarrumNodeChangeListener.canReact = true
            }
        }
    }


    static class MacmarrumNodeChangeListener implements NodeChangeListener {
        static boolean canReact = true
//        static final DateFormat df = new SimpleDateFormat('HH:mm:ss.S')

        void nodeChanged(NodeChanged event) {
            /* enum ChangedElement {TEXT, DETAILS, NOTE, ICON, ATTRIBUTE, FORMULA_RESULT, UNKNOWN} */
            if (!canReact)
                return
//        debug(":: ${df.format(new Date())} ${event.node.id} ${event.changedElement} ${event.node.transformedText}")
            canReact = false
            switch (event.changedElement) {
                case NodeChanged.ChangedElement.TEXT:
                    Utils.minimizeNodeIfTextIsLonger(event.node)
                    break
                case NodeChanged.ChangedElement.UNKNOWN:
//                    debug("<> UNKNOWN on ${event.node.id} ${event.node.text}")
//                    CompactLeaves.forBranch(event.node.parent) // folded/unfolded
                    break
            }
            canReact = true
        }
    }
}