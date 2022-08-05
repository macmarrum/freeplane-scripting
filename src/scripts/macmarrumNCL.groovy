// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac3"})
// Note: config is read only at start-up. If changed afterwards, the Listener needs to be restarted.

import org.freeplane.api.LengthUnit
import org.freeplane.api.Node as FPN
import org.freeplane.api.NodeChangeListener
import org.freeplane.api.NodeChanged
import org.freeplane.api.NodeChanged.ChangedElement
import org.freeplane.api.Quantity
import org.freeplane.features.edge.EdgeController
import org.freeplane.features.edge.mindmapmode.MEdgeController
import org.freeplane.features.map.MapModel
import org.freeplane.features.map.NodeModel
import org.freeplane.features.nodestyle.NodeBorderModel
import org.freeplane.plugin.script.FreeplaneScriptBaseClass.ConfigProperties

import java.awt.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.List

class MacmarrumNodeChangeListener implements NodeChangeListener {
    static boolean canReact = true
    static final DateFormat df = new SimpleDateFormat('HH:mm:ss.S')
    static String horizontalShift = '1 cm'
    static Quantity<LengthUnit> hGap = Quantity.fromString(horizontalShift, LengthUnit.px)
    static final controller = EdgeController.controller as MEdgeController
    static final config = new ConfigProperties()
    static final max_shortened_text_length = config.getIntProperty("max_shortened_text_length")

    static updateHGapIfNotNull(String horizontalShift) {
        if (horizontalShift != null) {
            this.horizontalShift = horizontalShift
            if (horizontalShift != 'none')
                hGap = Quantity.fromString(horizontalShift, LengthUnit.px)
        }
    }

    static minimizeNodeIfTextIsLonger(FPN node) {
        if (node.visible) {
            // use node.to to get the size of the resulting value (formula evaluated), not the underlying formula
            // NB. node.to triggers formula evaluation if core is not IFormattedObject, Number or Date
            node.minimized = node.to.plain.size() > max_shortened_text_length
        }
    }

    static setHorizontalShift(FPN node) {
        if (horizontalShift != 'none' && node.visible && node.horizontalShiftAsLength != hGap) {
//            println(">> setHorizontalShift")
            node.horizontalShift = hGap
        }
    }

    static int getGrandchildVisiblePosition(FPN leaf) {
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
        for (FPN child : parent.children) {
            if (child.id == leaf.id)
                return leafCount
            if (child.visible)
                leafCount++
        }
    }

    static applyEdgeColorsToBranchesAndAlteringColorsToLeafs(FPN node) {
        int colorCounter
        Color edgeColor
        Color bgColor
        MapModel mapModel = node.mindMap.delegate
        node.mindMap.root.children.eachWithIndex { level1, i ->
            colorCounter = i + 1
            edgeColor = controller.getEdgeColor(mapModel, colorCounter)
            level1.find { it.visible }.each { FPN it ->
                NodeModel nodeModel = it.delegate
                NodeBorderModel.createNodeBorderModel(nodeModel).borderColorMatchesEdgeColor = true
                it.style.edge.color = edgeColor
                if (it.isLeaf() && it.getNodeLevel(false) > 1)
                    bgColor = getGrandchildVisiblePosition(it) % 2 == 0 ? edgeColor.brighter() : edgeColor.darker()
                else
                    bgColor = edgeColor
                it.style.backgroundColor = bgColor
            }
        }
    }

    void nodeChanged(NodeChanged event) {
        /* enum ChangedElement {TEXT, DETAILS, NOTE, ICON, ATTRIBUTE, FORMULA_RESULT, UNKNOWN} */
        if (!canReact)
            return
//        println(":: ${df.format(new Date())} ${event.node.id} ${event.changedElement} ${event.node.transformedText}")
        canReact = false
        switch (event.changedElement) {
            case ChangedElement.UNKNOWN:
                setHorizontalShift(event.node)
                applyEdgeColorsToBranchesAndAlteringColorsToLeafs(event.node)
                break
            case ChangedElement.TEXT:
                minimizeNodeIfTextIsLonger(event.node)
                break
        }
        canReact = true
    }
}

def listeners = node.mindMap.listeners
String alias = 'macmarrum_NodeChangeListener'
List<FPN> foundlings = c.find { FPN it -> it.isGlobal && it.alias == alias }
FPN target = foundlings.size() == 1 ? foundlings[0] : null
if (!target) {
    target = node.mindMap.root.createChild(alias.replaceAll(/_/, ' '))
    target.left = true
    target.isGlobal = true
    target.alias = alias
}
String now = format(new Date(), X.dfLong)
String triangular_flag = 'emoji-1F6A9'
if (listeners.size() > 0) {
    listeners.each { if (it.class.name == 'MacmarrumNodeChangeListener') node.mindMap.removeListener(it) }
    target['off'] = now
    while (triangular_flag in target.icons.icons) {
        target.icons.remove(triangular_flag)
    }
} else {
    target['on'] = now
    if (triangular_flag !in target.icons.icons)
        target.icons.add(triangular_flag)
    MacmarrumNodeChangeListener.updateHGapIfNotNull(node.mindMap.root['h.shift']?.text)
    c.findAll().drop(1).each { FPN it ->
        MacmarrumNodeChangeListener.minimizeNodeIfTextIsLonger(it)
        MacmarrumNodeChangeListener.setHorizontalShift(it)
        MacmarrumNodeChangeListener.applyEdgeColorsToBranchesAndAlteringColorsToLeafs(it)
    }
    node.mindMap.addListener(new MacmarrumNodeChangeListener())
}
