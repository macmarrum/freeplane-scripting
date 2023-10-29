// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Import"})


import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.GPathResult
import org.freeplane.api.Node
import org.freeplane.plugin.script.proxy.ScriptUtils

def node = ScriptUtils.node()
//def imp = node.mindMap.root.createChild('imp')
//def file = new File('/home/m/ISO20022/iso-20022-message-definitions/pacs.008.001.11.xsd')
def file = new File('/home/m/ISO20022/iso-20022-message-definitions/pacs.009.001.10.xsd')
def schema = new XmlSlurper().parse(file)
process(schema, node.mindMap.root)

static void process(GPathResult xmlNode, Node node) {
    def tag = xmlNode.name()
    def xmlChildren = xmlNode.children()
    Node newChild
    switch (tag) {
        case ['element', 'complexType', 'simpleType', 'attribute'] -> {
            newChild = node.createChild(xmlNode.@name as String)
            processXmlAttributes(xmlNode, newChild)
        }
        case ['restriction', 'extension'] -> {
            newChild = node.createChild("${tag} | ${xmlNode.@base}" as String)
            processXmlAttributes(xmlNode, newChild)
        }
        case ['minLength'] -> {
            node['minLength'] = xmlNode.@value as String
        }
        case ['maxLength'] -> {
            node['maxLength'] = xmlNode.@value as String
        }
        default -> {
            // ['choice', 'sequence']
            if (xmlChildren.size() > 0) {
                newChild = node.createChild(tag)
                processXmlAttributes(xmlNode, newChild)
            } else { // childless nodes usually have @value
                // enumeration, pattern,  fractionDigits, totalDigits
                // --minLength,-maxLength--
                newChild = node.createChild(tag)
                newChild.createChild(xmlNode.@value as String)
            }
        }
    }
    if (node['minLength'] && node['maxLength']) {
        node.text = "${node.text} | <${node['minLength']}, ${node['maxLength']}>"
    }
    xmlChildren.each { process(it, newChild) }
}

static void processXmlAttributes(GPathResult elem, Node n) {
    n['tag'] = elem.name()
    def minOccurs = null
    def maxOccurs = null
    elem.attributes().each { String k, String v ->
        switch (k) {
            case 'name' -> n.text = v
            case ['type', 'value'] -> n.details = v
            case 'minOccurs' -> minOccurs = v == 'unbounded' ? '*' : v
            case 'maxOccurs' -> maxOccurs = v == 'unbounded' ? '*' : v
            default -> n[k] = v
        }
    }
    if (minOccurs != null && maxOccurs != null) {
        n.text = "${n.text} | {${minOccurs}, ${maxOccurs}}"
    }
}
