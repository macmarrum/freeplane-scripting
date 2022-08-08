import org.freeplane.api.Node
import org.freeplane.core.extension.IExtension
import org.freeplane.core.util.LogUtils
import org.freeplane.features.map.NodeModel
import org.freeplane.features.mode.Controller
import org.freeplane.plugin.script.ScriptingEngine

import java.text.SimpleDateFormat

/**
 * On root node, add a Node Conditional Style with a Script Filter:
 *   ScriptOnMapOpen.nodeConditionalStyle(node)
 * On root node, add an attribute named scriptOnMapOpen containing the script to be executed, e.g.
 *   MacmarrumChangeListenerUtils.toggleChangeListeners(node)
 *
 * The library saves on root node the extension ScriptOnMapOpenAlreadyRun to record that execution was already done.
 */
class ScriptOnMapOpen {
    static dfTime = new SimpleDateFormat('HH:mm:ss.S')

    static boolean nodeConditionalStyle(Node root) {
//        println(":: ${dfTime.format(new Date())} root conditional style")
        if (Controller.currentController.map === null) // if map isn't fully loaded yet
            return false
        NodeModel rootNodeModel = root.delegate
        if (rootNodeModel.getExtension(ScriptOnMapOpenAlreadyRun.class) === null) {
            rootNodeModel.addExtension(new ScriptOnMapOpenAlreadyRun())
            def script = root['scriptOnMapOpen']?.text
            if (script) {
                LogUtils.info("executing scriptOnMapOpen for ${root.mindMap.file.name}")
                ScriptingEngine.executeScript(rootNodeModel, script)
            }
        }
        return false
    }

    static class ScriptOnMapOpenAlreadyRun implements IExtension {}
}
