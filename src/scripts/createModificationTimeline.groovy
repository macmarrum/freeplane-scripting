// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})


import org.freeplane.core.ui.components.UITools
import org.freeplane.plugin.script.proxy.ScriptUtils
import org.freeplane.api.Node as FN

// date format should be sortable, if the nodes on the timeline are to be sorted
def DATE_FMT = 'yyyy-MM-dd'
def TIMELINE = 'Modification Timeline'

def mindMap = ScriptUtils.node().mindMap
def c = ScriptUtils.c()

FN timelineNode
timelineNode = mindMap.root.children.find { it.text == TIMELINE }
if (timelineNode) {
    c.select(timelineNode)
    UITools.showMessage("$TIMELINE node already exists\nDelete it or rename it first", 2)
} else {
    timelineNode = mindMap.root.createChild(TIMELINE)
    def dateToNode = new LinkedHashMap<String, FN>()
    for (FN n in mindMap.root.findAll()) {
        if (n.root || n == timelineNode)
            continue
        def dateStr = n.lastModifiedAt.format(DATE_FMT)
        if (!dateToNode.containsKey(dateStr)) {
            def dateNode = timelineNode.createChild(dateStr)
            dateToNode[dateStr] = dateNode
            dateNode['script1'] = """\
            Date.metaClass.nextDay = {
               use(groovy.time.TimeCategory) {
                  delegate + 1.day
               }
            }
            def date = Date.parse('${DATE_FMT}', '${dateStr}')
            def nextDay = date.nextDay()
            def shouldShowAncestors = true
            def shouldShowDescendants = false
            node.mindMap.filter(shouldShowAncestors, shouldShowDescendants, { it == node || (it.lastModifiedAt >= date && it.lastModifiedAt < nextDay) })
            menuUtils.executeMenuItems(['SelectFilteredNodesAction'])
            """.stripIndent()
        }
    }
    c.centerOnNode(timelineNode)
    c.select(timelineNode)
    timelineNode.sortChildrenBy { it.text }
}
