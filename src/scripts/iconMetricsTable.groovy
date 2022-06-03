// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
def root = node.mindMap.root
def iconToCount = [:]
def totalNodeCount = 0
root.findAll().each { node ->
    totalNodeCount++
    node.delegate.icons.each { icon ->
        def descr = icon.translatedDescription
        if (!iconToCount[descr])
            iconToCount[descr] = 0
        iconToCount[descr]++
    }
}
// prepare the report - a table of rows
def iconMetricsTable = []
iconToCount.each { entry ->
    iconMetricsTable << [entry.key, entry.value, entry.value / totalNodeCount * 100]
}
// ORDER BY % DESC, Count DESC, Icon
iconMetricsTable.sort { a,b -> b[2] <=> a[2] ?: b[1] <=> a[1] ?: a[0].toLowerCase() <=> b[0].toLowerCase() }
// make a report in the form of a markdown table
def sb = new StringBuilder()
sb << '| Icon | Count | % |\n'
sb << '|---|---:|---:|\n'
iconMetricsTable.each { row ->
    sb << '| ' << row[0] << ' | ' << row[1] << ' | ' << String.format('%.1f', row[2]) << '% |\n'
}
// write to a (new) node
def iconMetricsNode = root.children.findResult { it.text == 'Icon metrics' ? it : null } ?: root.createChild('Icon metrics')
iconMetricsNode.detailsContentType = 'markdown'
iconMetricsNode.details = sb.toString()
c.select(iconMetricsNode)
