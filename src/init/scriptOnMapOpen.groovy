import org.freeplane.core.util.LogUtils
import org.freeplane.plugin.script.proxy.ScriptUtils
import org.freeplane.plugin.script.ScriptingEngine

def c = ScriptUtils.c()
String script
c.openMindMaps.each { map ->
    script = map.root['scriptOnMapOpen']?.text
    if (script) {
        LogUtils.info("executing scriptOnMapOpen for ${map.file.name}")
        ScriptingEngine.executeScript(map.root.delegate, script)
    }
}
