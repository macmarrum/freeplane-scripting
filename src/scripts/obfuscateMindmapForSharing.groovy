// @ExecutionModes({ON_SINGLE_NODE})

import org.freeplane.api.Connector
import org.freeplane.api.Node
import org.freeplane.api.Node as FN
import org.freeplane.features.attribute.Attribute
import org.freeplane.features.attribute.AttributeController
import org.freeplane.features.attribute.NodeAttributeTableModel
import org.freeplane.features.attribute.mindmapmode.MAttributeController
import org.freeplane.features.format.FormattedObject
import org.freeplane.features.format.IFormattedObject
import org.freeplane.features.map.NodeModel
import org.freeplane.features.mode.Controller
import org.freeplane.plugin.script.proxy.ProxyUtils
import org.freeplane.plugin.script.proxy.ScriptUtils
import org.freeplane.view.swing.features.filepreview.ExternalResource
import org.freeplane.view.swing.features.filepreview.ViewerController

import java.nio.file.Files
import java.nio.file.StandardCopyOption

def node = ScriptUtils.node()
def file = node.mindMap.file
def obfuscatedFile = new File(file.parentFile, 'obfuscated~' + file.name)
Files.copy(file.toPath(), obfuscatedFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
def loader = ScriptUtils.c().mapLoader(obfuscatedFile)
loader.withView()
def mAttrCtrl = AttributeController.controller as MAttributeController
for (FN n in loader.mindMap.root.findAll()) {
    obfuscateCore(n)
    obfuscateDetails(n)
    obfuscateNote(n)
    NodeModel m = n.delegate
    obfuscateAttributes(m)
    obfuscateConnectors(n)
    obfuscateImagePath(m)
}

static x(CharSequence msg) {
    return msg.replaceAll(/\w/, 'x')
}

static xc(Connector c) {
    for (propertyName in ['sourceLabel', 'middleLabel', 'targetLabel']) {
        String label = c."$propertyName"
        if (label !== null)
            c.sourceLabel = x(label)
    }
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
        def i = 0
        for (attr in attributeTable.attributes) {
            def value = attr.value
            if (value instanceof IFormattedObject) {
                if (value instanceof FormattedObject) {
                    def foValue = value as FormattedObject
                    def newFormattedObject = new FormattedObject(x(foValue.object as String), foValue.pattern)
                    attr.value = newFormattedObject
                }
            } else if (!(value instanceof Number || value instanceof Date)) {
                def stringValue = value as String
                if (!stringValue.startsWith('='))
                    attr.value = x(stringValue)
            }
            i++
        }
    }
}

static obfuscateConnectors(Node n) {
    for (conn in n.connectorsOut) {
        xc(conn)
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
