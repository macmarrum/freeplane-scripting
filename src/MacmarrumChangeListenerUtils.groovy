import org.freeplane.api.*
import org.freeplane.features.edge.EdgeController
import org.freeplane.features.edge.mindmapmode.MEdgeController
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

    static toggleChangeListeners(Node root) {
        int n = toggleNodeChangeListner(root)
        int m = toggleMapChangeListener(root)
//        updateNode(root, n + m)
    }

    static int toggleNodeChangeListner(Node root) {
        def listeners = root.mindMap.listeners
        if (listeners.size() > 0) {
            listeners.each {
                if (it.class.simpleName == MacmarrumNodeChangeListener.class.simpleName)
                    root.mindMap.removeListener(it)
            }
            return 0
        } else {
//            root.findAll().drop(1).each { Node it -> X.minimizeNodeIfTextIsLonger(it) }
            root.mindMap.addListener(new MacmarrumNodeChangeListener())
            return 1
        }
    }

    static int toggleMapChangeListener(Node root) {
        def mapController = Controller.currentModeController.mapController
        def listeners = mapController.mapChangeListeners
        def macmarrumListeners = listeners.findAll {
            it.class.simpleName == MacmarrumMapChangeListener.class.simpleName
        }
        if (macmarrumListeners.size() == 0) {
            def mapChangeListner = new MacmarrumMapChangeListener(root)
            Utils.applyEdgeColorsToBranchesAndAlteringColorsToLeafs(root)
            root.findAll().drop(1).each { Node it -> Utils.setHorizontalShift(it) }
            mapController.addMapChangeListener(mapChangeListner)
            return 1
        } else {
            macmarrumListeners.each {
                mapController.removeMapChangeListener(it)
            }
            return 0
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
                    (0..<parentPosition).each { i ->
                        leafCount += grandparent.children[i].children.findAll { it.visible }.size()
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

        static applyEdgeColorsToBranchesAndAlteringColorsToLeafs(Node root) {
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

    static class MacmarrumMapChangeListener implements IMapChangeListener {
        static Node root

        MacmarrumMapChangeListener(Node root) {
            this.root = root
            Utils.pullAndUpdateHGapIfNotNull(root)
        }

        void onNodeInserted(NodeModel parent, NodeModel child, int newIndex) {
            def id = child.createID() // gets id or generates it
//            println("++ ${id}")
            def node = root.mindMap.node(id)
            Utils.setHorizontalShift(node)
            Utils.applyEdgeColorsToBranchesAndAlteringColorsToLeafs(root)
        }

        void onNodeMoved(NodeMoveEvent nodeMoveEvent) {
//            println("-> ${nodeMoveEvent.child.id}")
            Utils.applyEdgeColorsToBranchesAndAlteringColorsToLeafs(root)
        }

        void onNodeDeleted(NodeDeletionEvent nodeDeletionEvent) {
//            println("-- ${nodeDeletionEvent.node.id}")
            Utils.applyEdgeColorsToBranchesAndAlteringColorsToLeafs(root)
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