import org.freeplane.api.Node
import org.freeplane.core.extension.IExtension
import org.freeplane.core.util.LogUtils
import org.freeplane.features.map.NodeModel
import org.freeplane.features.mode.Controller
import org.freeplane.plugin.script.ScriptingEngine

import java.text.SimpleDateFormat

/**
 * On the root node, add a Node Conditional Style with a Script Filter:
 *   ScriptOnMapOpen.nodeConditionalStyle(node)
 * On the root node, add one or more attributes named "scriptOnMapOpen" (or starting with "scriptOnMapOpen")
 * Use `Tools->Edit script…` to enter the script content, e.g.
 *   MacmarrumChangeListenerUtils.toggleChangeListeners(node)
 *
 * Note: The library saves on the root node the extension ScriptOnMapOpenFlag to record that execution was already done
 */
class ScriptOnMapOpen {
    private static final dfTime = new SimpleDateFormat('HH:mm:ss.S')
    private static final scriptOnMapOpen = 'scriptOnMapOpen'

    static boolean nodeConditionalStyle(Node root) {
//        println(":: ${dfTime.format(new Date())} root conditional style")
        if (Controller.currentController.map === null) // if map isn't fully loaded yet
            return false
        NodeModel rootNodeModel = root.delegate
        if (rootNodeModel.getExtension(ScriptOnMapOpenFlag.class) === null) {
            rootNodeModel.addExtension(new ScriptOnMapOpenFlag())
            String script
            root.attributes.eachWithIndex { entry, i ->
                if (entry.key.startsWith(scriptOnMapOpen)) {
                    script = entry.value
                    if (script) {
                        LogUtils.info("executing ${entry.key} (#$i) for ${root.mindMap.file.name}")
                        ScriptingEngine.executeScript(rootNodeModel, script)
                    }
                }
            }
        }
        return false
    }

    static class ScriptOnMapOpenFlag implements IExtension {}
}
