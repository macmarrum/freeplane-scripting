package io.github.macmarrum.freeplane

import org.freeplane.api.MindMap
import org.freeplane.core.extension.IExtension
import org.freeplane.core.util.LogUtils
import org.freeplane.features.attribute.Attribute
import org.freeplane.features.attribute.NodeAttributeTableModel
import org.freeplane.features.map.IMapLifeCycleListener
import org.freeplane.features.map.MapModel
import org.freeplane.features.map.NodeModel
import org.freeplane.features.mode.Controller
import org.freeplane.plugin.script.ScriptingEngine
import org.freeplane.plugin.script.proxy.ScriptUtils

class MapLifeCycleListener implements IMapLifeCycleListener {
    static class ScriptOnMapOpenFlag implements IExtension {
        static IExtension getExtensionOf(NodeModel nodeModel) {
            def extensions = nodeModel.sharedExtensions.values()
            return extensions.find { it.class.name == this.name }
        }
    }

    static final SCRIPT_ON_MAP_OPEN_ATTR_NAME = 'scriptOnMapOpen'

    static executeScriptOnMapOpen(MapModel mapModel) {
        NodeModel rootNodeModel = mapModel.rootNode
        if (!ScriptOnMapOpenFlag.getExtensionOf(rootNodeModel)) {
            rootNodeModel.addExtension(new ScriptOnMapOpenFlag())
            String script
            def attributeTable = rootNodeModel.getExtension(NodeAttributeTableModel.class)
            if (attributeTable) {
                attributeTable.attributes.eachWithIndex { Attribute attr, int i ->
                    if (attr.name.startsWithIgnoreCase(SCRIPT_ON_MAP_OPEN_ATTR_NAME)) {
                        script = attr.value
                        if (script) {
                            LogUtils.info("   executing ${attr.name} (#$i) for ${mapModel?.file?.name ?: mapModel.rootNode.text}")
                            ScriptingEngine.executeScript(rootNodeModel, script)
                        }
                    }
                }
            }
        }
    }

    @Override
    void onCreate(MapModel map) {
        executeScriptOnMapOpen(map)
    }
}


Controller.currentModeController.controller.addMapLifeCycleListener(new MapLifeCycleListener())

ScriptUtils.c().openMindMaps.each { MindMap mindMap ->
    MapLifeCycleListener.executeScriptOnMapOpen(mindMap.delegate)
}
