import org.freeplane.api.*
import org.freeplane.api.Node as FN
import org.freeplane.core.extension.IExtension
import org.freeplane.features.edge.EdgeController
import org.freeplane.features.edge.mindmapmode.MEdgeController
import org.freeplane.features.map.*
import org.freeplane.features.mode.Controller
import org.freeplane.features.nodelocation.LocationModel
import org.freeplane.features.nodestyle.NodeBorderModel
import org.freeplane.plugin.script.FreeplaneScriptBaseClass
import org.freeplane.plugin.script.proxy.NodeProxy

import java.awt.*
import java.text.SimpleDateFormat
import java.util.List
import java.util.Map

class MacmarrumChangeListenerUtils {
    static dfLong = new SimpleDateFormat('yyyy-MM-dd,E HH:mm:ss')
    static final FEATURES_ATTR_NAME = 'MacmarrumFeatures'
    static final COUSIN_VGAP_ATTR_NAME = 'CousinVGap'
    static final DEFAULT_COUSIN_VGAP = new Quantity(10, LengthUnit.pt)
    static final HGAP_ATTR_NAME = 'HGap'
    static final DEFAULT_HGAP = new Quantity(30, LengthUnit.pt)
    static final ID_LOCATION_ATTR_NAME = 'IdLocation'
    static final DEFAULT_ID_LOCATION = IdLocation.CORE
    static enum Feature {
        MINI_NODES, BRANCH_COLORS, HGAP, COUSIN_VGAP, ID_ON_CREATE
    }

    static enum IdLocation {
        CORE, DETAILS, NOTE
    }

    static debug(String... args) {
        println(args.collect { it.replaceAll('\n', ' | ') }.join(' '))
    }

    static int toggleChangeListeners(FN root) {
        int n = toggleNodeChangeListener(root)
        int m = toggleMapChangeListener(root)
//        updateInformationNode(root, n + m)
        return n + m
    }

    static int toggleNodeChangeListener2(FN root) {
        def mapController = Controller.currentModeController.mapController
        def macmarrumListeners = mapController.nodeChangeListeners.findAll {
            it.class.name == MacmarrumNodeChangeListener2.class.name
        }
        if (macmarrumListeners.size() == 0) {
            mapController.addNodeChangeListener(new MacmarrumNodeChangeListener2())
            debug(">> toggleNodeChangeListener => ON")
            return 1
        } else {
            for (nodeChangeListener in macmarrumListeners) {
                mapController.removeNodeChangeListener(nodeChangeListener)
            }
            debug(">> toggleNodeChangeListener => OFF")
            return 0
        }
    }

    static int toggleNodeChangeListener(FN root) {
        def macmarrumListeners = root.mindMap.listeners.findAll {
            it.class.name == MacmarrumNodeChangeListener.class.name
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
            CousinVGap.forBranch(root)
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

    static isEnabled(Feature feature, FN root) {
        return root[FEATURES_ATTR_NAME]?.text?.find("\\b${feature}\\b")
    }

    static class Utils {
        static final controller = EdgeController.controller as MEdgeController

        static getHGap(FN root) {
            def value = root[HGAP_ATTR_NAME].text
            return value ? Quantity.fromString(value, LengthUnit.pt) : DEFAULT_HGAP
        }

        static setHorizontalShift(FN node) {
            def root = node.mindMap.root
            if (isEnabled(Feature.HGAP, root)) {
                def hGap = getHGap(root)
                if (node.horizontalShiftAsLength != hGap && node.visible && !node.free) {
//                    debug(">> setHorizontalShift")
                    node.horizontalShift = hGap
                }
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
                def desiredMinimizedState = node.to.plain.size() > max_shortened_text_length
                if (node.minimized != desiredMinimizedState)
                    node.minimized = desiredMinimizedState
            }
        }

        static putNodeId(FN node) {
            def root = node.mindMap.root
            if (isEnabled(Feature.ID_ON_CREATE, root)) {
                switch (getIdLocation(root)) {
                    case IdLocation.CORE:
                        node.text = "${node.id} "
                        break
                    case IdLocation.DETAILS:
                        node.details = node.id
                        break
                    case IdLocation.NOTE:
                        node.note = node.id
                        break
                }
            }
        }

        static getIdLocation(Node root) {
            IdLocation idLocation
            try {
                idLocation = (root[ID_LOCATION_ATTR_NAME].text ?: 'N*U*L*L') as IdLocation
            } catch (ignored) {
                idLocation = DEFAULT_ID_LOCATION
            }
            return idLocation
        }
    }


    static class CousinVGap {
        static final VERTICAL_SHIFT_NONE = LocationModel.NULL_LOCATION.shiftY

        static debug(String... args) {
//            println(args.join(' '))
        }

        static forBranch(FN branch) {
            if (!isEnabled(Feature.COUSIN_VGAP, branch.mindMap.root))
                return
            debug('CousinVGap.forBranch', branch.id, branch.transformedText)
            final COUSIN_VGAP = getCousinVGap(branch)
            def m = createNodeToVisibleNonFreeChildren(branch)
            m.each { entry ->
                def node = entry.key
                def children = entry.value
                if (children.size() > 0) {
                    if (!node.folded) {
                        if (children.every { isApparentLeaf(it, m[it]) }) {
                            children.each { FN child ->
                                if (child.visible) // ignore summary nodes
                                    setVerticalShiftIfDifferent(child, VERTICAL_SHIFT_NONE, ':1:')
                            }
                        } else {
                            children.eachWithIndex { FN child, int i ->
                                if (child.visible) { // ignore summary nodes
                                    if (i == 0) {
                                        setVerticalShiftIfDifferent(child, VERTICAL_SHIFT_NONE, ':0:')
                                    } else {
                                        def prevChild = getPreviousVisible(i, children)
                                        if (prevChild) {
                                            if (isApparentLeaf(child, m[child]) && isApparentLeaf(prevChild, m[prevChild]))
                                                setVerticalShiftIfDifferent(child, VERTICAL_SHIFT_NONE, ':2:')
                                            else
                                                setVerticalShiftIfDifferent(child, COUSIN_VGAP, ':2:')
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

        static getCousinVGap(FN root) {
            def value = root[COUSIN_VGAP_ATTR_NAME].text
            return value ? Quantity.fromString(value, LengthUnit.pt) : DEFAULT_COUSIN_VGAP
        }

        static isApparentLeaf(FN node, List<FN> children) {
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
            if (child.verticalShiftAsLength != verticalShift) {
                def indicator = verticalShift == VERTICAL_SHIFT_NONE ? '||' : '<>'
                debug(debugPrefix, indicator, child.text, child.id, '->', verticalShift.toString())
                child.verticalShift = verticalShift
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

        static debug(String msg) {
//            println(msg)
        }

        static FN getRootIfListenerIsEnabled(MapModel mapModel) {
            return MacmarrumMapChangeListenerEnablerForMap.getExtensionOf(mapModel.rootNode)?.root
        }

        static boolean isRegularMap(FN root, NodeModel changedNode) {
            return root.id == changedNode.map.rootNode.id
        }

        void onNodeInserted(NodeModel parent, NodeModel child, int newIndex) {
            FN root = getRootIfListenerIsEnabled(child.map)
            if (root && isRegularMap(root, child)) {
                def id = child.createID() // gets id or generates it
                debug("++ ${id}")
                def node = root.mindMap.node(id)
                MacmarrumNodeChangeListener.canReact = false
                Utils.setHorizontalShift(node)
                def parentNode = root.mindMap.node(parent.ID)
                CousinVGap.forBranch(parentNode.parent ?: parentNode) // grandparent if exists, else parent - to cover `0 -> 1` children
                Utils.applyEdgeColorsToBranchesAndAlteringColorsToLeafsInMap(root)
                Utils.putNodeId(node)
                MacmarrumNodeChangeListener.canReact = true
            }
        }

        void onNodeMoved(NodeMoveEvent nodeMoveEvent) {
            FN root = getRootIfListenerIsEnabled(nodeMoveEvent.child.map)
            if (root && isRegularMap(root, nodeMoveEvent.child)) {
                debug("-> ${nodeMoveEvent.child.id}")
                MacmarrumNodeChangeListener.canReact = false
                CousinVGap.forBranch(root.mindMap.node(nodeMoveEvent.oldParent.ID))
                if (nodeMoveEvent.oldParent.ID != nodeMoveEvent.newParent.ID)
                    CousinVGap.forBranch(root.mindMap.node(nodeMoveEvent.newParent.ID))
                Utils.applyEdgeColorsToBranchesAndAlteringColorsToLeafsInMap(root)
                MacmarrumNodeChangeListener.canReact = true
            }
        }

        void onNodeDeleted(NodeDeletionEvent nodeDeletionEvent) {
            FN root = getRootIfListenerIsEnabled(nodeDeletionEvent.node.map)
            if (root && isRegularMap(root, nodeDeletionEvent.node)) {
                debug("-- ${nodeDeletionEvent.node.id}")
                MacmarrumNodeChangeListener.canReact = false
                def parent = root.mindMap.node(nodeDeletionEvent.parent.ID)
                CousinVGap.forBranch(parent.parent ?: parent) // grandparent if exists, else parent - to cover `1 -> 0` children
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
//                case NodeChanged.ChangedElement.UNKNOWN: // folded/unfolded not covered here
//                    debug("<> UNKNOWN on ${event.node.id} ${event.node.text}")
//                    CousinVGap.forBranch(event.node.parent) // folded/unfolded
//                    break
            }
            canReact = true
        }
    }

    static class MacmarrumNodeChangeListener2 implements INodeChangeListener {
        static canReact = true

        void debug(String... args) {
//            println(args.join(' '))
        }

        @Override
        void nodeChanged(NodeChangeEvent event) {
            debug(">> MacmarrumNodeChangeListener2.nodeChanged canReact:$canReact ${event.property} ${event.node.text} ${event.node.createID()}")
            if (!canReact)
                return
            canReact = false
            def node = new NodeProxy(event.node, null) as FN
            switch (event.property) {
                case NodeModel.NODE_TEXT:
                    Utils.minimizeNodeIfTextIsLonger(node)
                    break
                case NodeModel.NodeChangeType.FOLDING:
                    if (!node.root)
                        CousinVGap.forBranch(node.parent)
                    break
            }
            canReact = true
        }
    }
}