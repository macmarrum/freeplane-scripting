// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})


import org.freeplane.api.MindMap
import org.freeplane.features.styles.MapStyleModel
import org.freeplane.plugin.script.proxy.MapProxy
import org.freeplane.plugin.script.proxy.ScriptUtils

import javax.swing.*

import static org.freeplane.features.styles.MapStyleModel.STYLES_USER_DEFINED

def node = ScriptUtils.node()
def c = ScriptUtils.c()

def SEP = ' | '

def mindMaps = c.openMindMaps.sort()
def mindMapNames = []
mindMaps.eachWithIndex { MindMap it, int i ->
    if (it != node.mindMap)
        mindMapNames << "${i}${SEP}${it.name}"
}
String selectedMindMapName = JOptionPane.showInputDialog(null, null, 'Select the source mind map',
        JOptionPane.QUESTION_MESSAGE, null, mindMapNames.toArray(), null)
if (selectedMindMapName == null || selectedMindMapName == '') {
    c.statusInfo = 'selectedMindMapName is null or blank'
    return
} else {
    def i = selectedMindMapName.tokenize(SEP)[0] as Integer
    def mindMap = mindMaps[i]
    def mapStyleModel = MapStyleModel.getExtension((mindMap as MapProxy).delegate)
    def userStyleParentNode = mapStyleModel.getStyleNodeGroup(mapStyleModel.styleMap, STYLES_USER_DEFINED)
    String[] styleNames = userStyleParentNode.children.collect { it.text }.toArray()
    String selectedStyleName = JOptionPane.showInputDialog(null, null, 'Select the style',
            JOptionPane.QUESTION_MESSAGE, null, styleNames, null)
    if (selectedStyleName == null || selectedStyleName == '') {
        c.statusInfo = 'selectedStyleName is null or blank'
        return
    } else {
        node.mindMap.copyStyleFrom(mindMap, selectedStyleName)
    }
}
