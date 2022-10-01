// @ExecutionModes({ON_SINGLE_NODE})


import org.freeplane.api.Node
import org.freeplane.api.Node as FN
import org.freeplane.core.ui.components.UITools
import org.freeplane.features.attribute.NodeAttributeTableModel
import org.freeplane.features.format.IFormattedObject
import org.freeplane.features.map.NodeModel
import org.freeplane.features.mode.Controller
import org.freeplane.plugin.script.FreeplaneScriptBaseClass
import org.freeplane.plugin.script.proxy.ScriptUtils
import org.freeplane.view.swing.features.filepreview.ExternalResource
import org.freeplane.view.swing.features.filepreview.ViewerController

import javax.swing.*

def c = ScriptUtils.c()
def node = ScriptUtils.node()
def config = new FreeplaneScriptBaseClass.ConfigProperties()

final hasWritePermission = config.getBooleanProperty('execute_scripts_without_write_restriction')
final saveTitle = 'Save operation aborted'
final saveMessage = '''
The mindmap needs to be saved to disk manually, because the write permission for scripts is missing
You can enable "Preferences…->Plugins->Scripting->Permit file/write operations (NOT recommended)" and the script will save mindmaps automatically
'''
if (!node.mindMap.saved) {
    if (!hasWritePermission) {
        UITools.informationMessage(UITools.currentFrame, 'You need to save your mindmap before running the script' + saveMessage, saveTitle, JOptionPane.WARNING_MESSAGE)
        return
    }
    final allowInteraction = true
    node.mindMap.save(allowInteraction)
}

def file = node.mindMap.file
def mindMap = c.mapLoader(file).withView().unsetMapLocation().mindMap
mindMap.root.findAll().each { FN n ->
    obfuscateCore(n)
    obfuscateDetails(n)
    obfuscateNote(n)
    NodeModel m = n.delegate
    obfuscateAttributes(m)
    obfuscateConnectors(n)
    obfuscateImagePath(m)
}

def newName = 'obfuscated~' + file.name
def newStem = newName.replaceAll(/\.mm$/, '')
mindMap.name = newStem
mindMap.root.text = newStem
def targetFile = new File(file.parentFile, newName)
if (!hasWritePermission) {
    UITools.informationMessage(UITools.currentFrame, newName + saveMessage, saveTitle, JOptionPane.WARNING_MESSAGE)
} else {
    def isOkToSave = !file.exists()
    if (!isOkToSave && confirmOverwrite(targetFile, node.delegate))
        isOkToSave = true
    if (isOkToSave)
        mindMap.saveAs(targetFile)
}


static x(CharSequence msg) {
    return msg.replaceAll(/\w/, 'x')
}

static obfuscateCore(Node n) {
    if (n.text.startsWith('<html>'))
        n.text = x(n.to.plain)
    else if (!n.text.startsWith('='))
        n.text = x(n.text)
}

static obfuscateDetails(Node n) {
    def details = n.details?.text
    if (details && !details.startsWith('='))
        n.details = x(details)
}

static obfuscateNote(Node n) {
    def note = n.note?.text
    if (note && !note.startsWith('='))
        n.note = x(note)
}

static obfuscateAttributes(NodeModel m) {
    NodeAttributeTableModel attributeTable = m.getExtension(NodeAttributeTableModel.class)
    if (attributeTable !== null) {
        for (attr in attributeTable.attributes) {
            def value = attr.value
            if (!(value instanceof IFormattedObject || value instanceof Number || value instanceof Date)) {
                def stringValue = value as String
                if (!stringValue.startsWith('='))
                    attr.value = x(stringValue)
            }
        }
    }
}

static obfuscateConnectors(Node n) {
    for (conn in n.connectorsOut) {
        for (propertyName in ['sourceLabel', 'middleLabel', 'targetLabel']) {
            String label = conn."$propertyName"
            if (label !== null)
                conn.sourceLabel = x(label)
        }
    }
}

static obfuscateImagePath(NodeModel m) {
    def extResource = m.getExtension(ExternalResource.class)
    if (extResource) {
        def uriString = extResource.uri.toString()
        def uriList = uriString.split('/')
        def uriListSize = uriList.size()
        def result = []
        uriList.eachWithIndex { part, i ->
            result << (i > 0 && i < uriListSize - 1 ? x(part) : part)
        }
        def obfuscatedUri = result.join('/').toURI()
        def newExtResource = new ExternalResource(obfuscatedUri)
        def vc = Controller.currentController.modeController.getExtension(ViewerController.class)
        vc.undoableDeactivateHook(m)
        vc.undoableActivateHook(m, newExtResource)
    }
}

static boolean confirmOverwrite(File targetFile, NodeModel sourceModel) {
    def title = 'Confirm overwrite'
    def msg = "The file already exists:\n${targetFile.name}\nOverwire it?"
    def decision = UITools.showConfirmDialog(sourceModel, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)
    return decision == 0
}
