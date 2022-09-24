package io.github.macmarrum.freeplane

import org.freeplane.api.MindMap
import org.freeplane.core.util.LogUtils
import org.freeplane.features.attribute.Attribute
import org.freeplane.features.attribute.NodeAttributeTableModel
import org.freeplane.features.map.IMapLifeCycleListener
import org.freeplane.features.map.MapModel
import org.freeplane.features.mode.Controller
import org.freeplane.plugin.script.ScriptingEngine
import org.freeplane.plugin.script.proxy.ScriptUtils

class MapOpenListener implements IMapLifeCycleListener {
    static final SCRIPT_ON_MAP_OPEN_ATTR_NAME = 'scriptOnMapOpen'

    static executeScriptOnMapOpen(MapModel map) {
        def root = map.rootNode
        String script
        def attributeTable = root.getExtension(NodeAttributeTableModel.class)
        attributeTable?.attributes?.eachWithIndex { Attribute attr, int i ->
            if (attr.name.startsWithIgnoreCase(SCRIPT_ON_MAP_OPEN_ATTR_NAME)) {
                script = attr.value
                if (script) {
                    LogUtils.info("   executing ${attr.name} (#$i) in ${map?.file?.name ?: map.rootNode.text}")
                    ScriptingEngine.executeScript(root, script)
                }
            }
        }
    }

    @Override
    void onCreate(MapModel map) {
        executeScriptOnMapOpen(map)
    }
}


ScriptUtils.c().openMindMaps.each { MindMap mindMap ->
    MapOpenListener.executeScriptOnMapOpen(mindMap.delegate)
}

Controller.currentModeController.controller.addMapLifeCycleListener(new MapOpenListener())
