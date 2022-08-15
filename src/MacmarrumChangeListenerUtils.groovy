import org.freeplane.api.*
import org.freeplane.core.extension.IExtension
import org.freeplane.features.edge.EdgeController
import org.freeplane.features.edge.mindmapmode.MEdgeController
import org.freeplane.features.filter.FilterController
import org.freeplane.features.map.*
import org.freeplane.features.mode.Controller
import org.freeplane.features.nodestyle.NodeBorderModel
import org.freeplane.plugin.script.FreeplaneScriptBaseClass

import java.awt.*
import java.text.SimpleDateFormat
import java.util.List

class MacmarrumChangeListenerUtils {
    static dfLong = new SimpleDateFormat('yyyy-MM-dd,E HH:mm:ss')

    /*
     * Note: config is read only at start-up. If changed afterwards, the Listener needs to be restarted.
     */

    static int toggleChangeListeners(Node root) {
        int n = toggleNodeChangeListener(root)
        int m = toggleMapChangeListener(root)
//        updateNode(root, n + m)
        return n + m
    }

    static int toggleNodeChangeListener(Node root) {
        def macmarrumListeners = root.mindMap.listeners.findAll {
            it.class.simpleName == MacmarrumNodeChangeListener.class.simpleName
        }
        if (macmarrumListeners.size() == 0) {
            root.mindMap.addListener(new MacmarrumNodeChangeListener())
            println(":: toggleNodeChangeListener => ON")
            return 1
        } else {
            macmarrumListeners.each {
                root.mindMap.removeListener(it)
            }
            println(":: toggleNodeChangeListener => OFF")
            return 0
        }
    }

    static int toggleMapChangeListener(Node root) {
        def mapController = Controller.currentModeController.mapController
        def macmarrumListeners = mapController.mapChangeListeners.findAll {
            it.class.simpleName == MacmarrumMapChangeListener.class.simpleName
        }
        NodeModel rootModel = root.delegate
        def macmarrumMapChangeListenerEnablerForMap = MacmarrumMapChangeListenerEnablerForMap.getExtensionOf(rootModel)
        if (macmarrumMapChangeListenerEnablerForMap === null) {
            rootModel.addExtension(new MacmarrumMapChangeListenerEnablerForMap(root))
            println(":: toggleMapChangeListener for ${root.mindMap.file.name} => ON")
            Utils.applyEdgeColorsToBranchesAndAlteringColorsToLeafsInMap(root)
            root.findAll().drop(1).each { Node it -> Utils.setHorizontalShift(it) }
            enableMapChangeListenerIfNotYetEnabled(macmarrumListeners, mapController)
            return 1
        } else {
            def extensions = rootModel.sharedExtensions.values()
            extensions.collect().each {
                if (it.class.name == MacmarrumMapChangeListenerEnablerForMap.class.name && it.root == root) {
                    extensions.remove(it)
                    println(":: toggleMapChangeListener for ${root.mindMap.file.name} => OFF")
                }
            }
            return 0
        }
    }

    static boolean enableMapChangeListenerIfNotYetEnabled(Collection<IMapChangeListener> macmarrumListeners, MapController mapController) {
        if (macmarrumListeners.size() == 0) {
            mapController.addMapChangeListener(new MacmarrumMapChangeListener())
            println(":: enableMapChangeListenerIfNotYetEnabled => ON")
            return true
        } else {
            println(":: enableMapChangeListenerIfNotYetEnabled => already enabled")
            return false
        }
    }

    static disableMapChangeListener(Collection<IMapChangeListener> macmarrumListeners, MapController mapController) {
        macmarrumListeners.each {
            mapController.removeMapChangeListener(it)
        }
    }

    static updateNode(Node root, int listnersCount) {
        String alias = 'macmarrumChangeListeners'
        List<Node> foundlings = root.find { Node it -> it.isGlobal && it.alias == alias }
        Node target = foundlings.size() == 1 ? foundlings[0] : null
        if (!target) {
            target = root.createChild(alias.replaceAll(/_/, ' '))
            target.left = true
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

    static class Utils {
        static String horizontalShift = '1 cm'
        static Quantity<LengthUnit> hGap = Quantity.fromString(horizontalShift, LengthUnit.px)
        static final controller = EdgeController.controller as MEdgeController

        static pullAndUpdateHGapIfNotNull(Node root) {
            def horizontalShift = root['h.shift']?.text
            if (horizontalShift != null) {
                this.horizontalShift = horizontalShift
                if (horizontalShift != 'none')
                    hGap = Quantity.fromString(horizontalShift, LengthUnit.px)
            }
        }

        static setHorizontalShift(Node node) {
            if (horizontalShift != 'none' && node.visible && node.horizontalShiftAsLength != hGap) {
//            println(">> setHorizontalShift")
                node.horizontalShift = hGap
            }
        }

        static int getGrandchildVisiblePosition(Node leaf) {
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
            for (Node child : parent.children) {
                if (child.id == leaf.id)
                    return leafCount
                if (child.visible)
                    leafCount++
            }
        }

        static applyEdgeColorsToBranchesAndAlteringColorsToLeafsInMap(Node root) {
            int colorCounter
            Color edgeColor
            Color bgColor
            MapModel mapModel = root.mindMap.delegate
            root.children.eachWithIndex { level1, i ->
                colorCounter = i + 1
                edgeColor = controller.getEdgeColor(mapModel, colorCounter)
                level1.find { it.visible }.each { Node it ->
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

        static minimizeNodeIfTextIsLonger(Node node) {
            if (node.visible) {
                // use node.to to get the size of the resulting value, not the underlying formula
                // NB. node.to triggers formula evaluation if core is not IFormattedObject, Number or Date
                node.minimized = node.to.plain.size() > max_shortened_text_length
            }
        }
    }

    static class MacmarrumMapChangeListenerEnablerForMap implements IExtension {
        public Node root

        MacmarrumMapChangeListenerEnablerForMap(Node root) {
            this.root = root
        }

        static IExtension getExtensionOf(NodeModel nodeModel) {
            def extensions = nodeModel.sharedExtensions.values()
//            println(extensions.collect{it.class.simpleName})
            def ext = extensions.find { it.class.name == this.name }
//            println("== MacmarrumMapChangeListenerEnablerForMap is ${ext ? 'not ' : ''}null")
            return ext
        }
    }

    static class MacmarrumMapChangeListener implements IMapChangeListener {

        static boolean isVisible(NodeModel node) {
            return node.hasVisibleContent(FilterController.getFilter(node.map))
        }

        static IExtension getEnabler(MapModel mapModel) {
            return MacmarrumMapChangeListenerEnablerForMap.getExtensionOf(mapModel.rootNode)
        }

        void onNodeInserted(NodeModel parent, NodeModel child, int newIndex) {
            def macmarrumMapChangeListenerEnablerForMap = getEnabler(child.map)
            if (macmarrumMapChangeListenerEnablerForMap && isVisible(child)) {
                def id = child.createID() // gets id or generates it
//                println("++ ${id}")
                def root = macmarrumMapChangeListenerEnablerForMap.root
                def node = root.mindMap.node(id)
                Utils.setHorizontalShift(node)
                Utils.applyEdgeColorsToBranchesAndAlteringColorsToLeafsInMap(root)
            }
        }

        void onNodeMoved(NodeMoveEvent nodeMoveEvent) {
            def macmarrumMapChangeListenerEnablerForMap = getEnabler(nodeMoveEvent.child.map)
            if (macmarrumMapChangeListenerEnablerForMap && isVisible(nodeMoveEvent.child)) {
//                println("-> ${nodeMoveEvent.child.id}")
                def root = macmarrumMapChangeListenerEnablerForMap.root
                Utils.applyEdgeColorsToBranchesAndAlteringColorsToLeafsInMap(root)
            }
        }

        void onNodeDeleted(NodeDeletionEvent nodeDeletionEvent) {
            def macmarrumMapChangeListenerEnablerForMap = getEnabler(nodeDeletionEvent.node.map)
            if (macmarrumMapChangeListenerEnablerForMap && isVisible(nodeDeletionEvent.node)) {
//                println("-- ${nodeDeletionEvent.node.id}")
                def root = macmarrumMapChangeListenerEnablerForMap.root
                Utils.applyEdgeColorsToBranchesAndAlteringColorsToLeafsInMap(root)
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
//        println(":: ${df.format(new Date())} ${event.node.id} ${event.changedElement} ${event.node.transformedText}")
            canReact = false
            switch (event.changedElement) {
                case NodeChanged.ChangedElement.TEXT:
                    Utils.minimizeNodeIfTextIsLonger(event.node)
                    break
            }
            canReact = true
        }
    }
}