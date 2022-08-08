import org.freeplane.api.Node
import org.freeplane.core.util.LogUtils
import org.freeplane.features.map.NodeModel
import org.freeplane.features.mode.Controller
import org.freeplane.features.url.LastChoosenDirectory
import org.freeplane.plugin.script.ScriptingEngine

import java.text.SimpleDateFormat

/**
 * On root node, add a Node Conditional Style with a Script Filter:
 *   ScriptOnMapOpen.nodeConditionalStyle(node)
 * On root node, add an attribute named scriptOnMapOpen containing the script to be executed, e.g.
 *   MacmarrumChangeListenerUtils.toggleChangeListeners(node)
 *
 * There is little possibility for a script to make sure it runs only once.
 * The library uses LastChoosenDirectory.class to record its execution. Freeplane uses the class on the map level,
 * but not on the node level, so no conflict there.
 */
class ScriptOnMapOpen {
    static dfTime = new SimpleDateFormat('HH:mm:ss.S')

    static boolean nodeConditionalStyle(Node root) {
//        println(":: ${dfTime.format(new Date())} root conditional style")
        if (Controller.currentController.map === null) // if map isn't fully loaded yet
            return false
        NodeModel rootNodeModel = root.delegate
        if (rootNodeModel.getExtension(LastChoosenDirectory.class) === null) {
            rootNodeModel.addExtension(new LastChoosenDirectory())
            def script = root['scriptOnMapOpen']?.text
            if (script) {
                LogUtils.info("executing scriptOnMapOpen for ${root.mindMap.file.name}")
                ScriptingEngine.executeScript(rootNodeModel, script)
            }
        }
        return false
    }
}
