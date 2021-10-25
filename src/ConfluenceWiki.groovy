/*
 * Inspired by https://github.com/EdoFro/Freeplane_MarkdownHelper
 */
import org.freeplane.api.Node as FPN

class ConfluenceWiki {

    static HashMap<String, String> style = [
            root: 'cWikiRoot',
            leaf: 'cWikiLeaf',
            node: 'cWikiNode'
    ]

    static HashMap<String, String> icon = [
            noEntry                 : 'emoji-26D4',
            eol_chequeredFlag       : 'emoji-1F3C1',
            noSep_cancer            : 'emoji-264B',
            pButton                 : 'emoji-1F17F',
            nl_rightArrowCurvingDown: 'emoji-2935',
            ol_keycapHash           : 'emoji-0023-20E3',
            border_unchecked        : 'unchecked',
    ]

    static Boolean isLeaf(FPN n) {
        return n.style.name == style.leaf
    }

    static Boolean hasIcon(FPN n, String icon) {
        return n.icons.icons.contains(icon)
    }

    static String getContent(FPN n) {
        return n.note ?: n.transformedText
    }

    static String getSep(FPN n) {
        return hasIcon(n, icon.noSep_cancer) ? '' : ' '
    }

    static String getNewLine(FPN n) {
        return hasIcon(n, icon.nl_rightArrowCurvingDown) ? '\n' : ''
    }

    static String getEol(FPN n) {
        return hasIcon(n, icon.eol_chequeredFlag) ? '\n' : ''
    }

    static String mkParent(FPN n) {
        return _execIfChildren(n, { _mkParent(n) })
    }

    static String _execIfChildren(FPN n, Closure closure) {
        if (n.children.size() > 0)
            return closure()
        else
            return '<!-- children are missing -->'
    }

    static String _mkParent(FPN n) {
        return n.children.collect { mkNode(it) }.join('')
    }

    static String mkNode(FPN n) {
        def eol = getEol(n)
        def nl = getNewLine(n)
        if (hasIcon(n, icon.noEntry)) {
            return ''
        } else {
            if (isLeaf(n)) {
                return "${n.note}${eol}"
            } else {
                def body = "${getContent(n)}${getSep(n)}${n.children.collect { mkNode(it) }.join('')}"
                if (_isHeading(n)) {
                    return _mkHeading(n, body, nl, eol)
                } else if (hasIcon(n, icon.pButton)) {
                    return "<p>${nl}${body}${nl}</p>${eol}".toString()
                } else {
                    return "${body}${eol}".toString()
                }
            }
        }
    }

    static Boolean _isHeading(FPN n) {
        return (n.icons.size() > 0 && n.icons.icons.any { it.startsWith('full-') })
    }

    static String _mkHeading(FPN n, String body, String nl, String eol) {
        def hIcon = n.icons.icons.find { it.startsWith('full-') }
        def hLevel = hIcon[5..-1]
        return "<h${hLevel}>${nl}${body}${nl}</h${hLevel}>${eol}".toString()
    }


    static FPN getFirstChildIfNotIgnoreNode(FPN n, Boolean canSkipLeafCheck = false) {
        def isLeafAndNotSkipped = isLeaf(n) && !canSkipLeafCheck
        if (isLeafAndNotSkipped || n.children.size() == 0 || hasIcon(n.children[0], icon.noEntry))
            return null
        else
            return n.children[0]
    }

    static int countFirstChildChain(FPN n, int cnt = 0) {
        def child = getFirstChildIfNotIgnoreNode(n)
        if (child)
            return countFirstChildChain(child, ++cnt)
        else
            return cnt
    }

    enum HiLite1st {
        ROW, COLUMN, NONE
    }

    static String mkTable(FPN n) {
        def nl = getNewLine(n)
        HiLite1st hiLite1st
        if (n['hiLite1st']) {
            hiLite1st = HiLite1st.valueOf(n['hiLite1st'].toString().toUpperCase())
        } else {
            hiLite1st = HiLite1st.NONE
        }
        def tableWiki = new StringBuilder()
        def colNum = 1
        def rowNum = 1
        tableWiki << "<table>${nl}<colgroup><col /><col /></colgroup>${nl}<tbody>${nl}"
        // the first column in each row is technical, therefore it's skipped
        n.children.each { FPN row ->
            if (!hasIcon(row, icon.noEntry)) {  // not ignoreNode
                row.details = "₵${countFirstChildChain(row)}".toString()

                if (row.children.size() > 0) {
                    tableWiki << "<tr>${nl}"
                    tableWiki << mkTableCell(row.children[0], rowNum, colNum, hiLite1st, nl)
                    tableWiki << "</tr>${nl}"  // close the row
                }
                rowNum++
            }
        }
        tableWiki << "</tbody>${nl}</table>"
        def result = tableWiki.toString()
        return result
    }

    static String mkTableCell(FPN n, int rowNum, int colNum, HiLite1st hiLite1st, String nl) {
        n.details = "№$colNum"
        def result = new StringBuilder()
        def tag
        switch (hiLite1st) {
            case HiLite1st.ROW:
                tag = rowNum == 1 ? '<th>' : '<td>'
                break
            case HiLite1st.COLUMN:
                tag = colNum == 1 ? '<th>' : '<td>'
                break
            case HiLite1st.NONE:
                tag = '<td>'
        }
        result << tag
        result << getContent(n)
        result << "</${tag[1..-1]}${nl}"
        FPN firstChildIfNotIgnoreNode = getFirstChildIfNotIgnoreNode(n)
        if (firstChildIfNotIgnoreNode) {
            result << mkTableCell(firstChildIfNotIgnoreNode, rowNum, ++colNum, hiLite1st, nl)
        }
        return result.toString()
    }

    static String mkList(FPN n) {
        def nl = getNewLine(n)
        def result = new StringBuilder()
        def tag = hasIcon(n, icon.ol_keycapHash) ? '<ol>' : '<ul>'
        result << "${tag}${nl}"
        n.children.each {
            result << "<li>${nl}${mkNode(it)}${nl}</li>${nl}"
        }
        result << "</${tag[1..-1]}"
        return result.toString()
    }

    static String mkZipList(FPN n) {
        def nl = getNewLine(n)
        def bullet = n['simBullet'] ?: '●'
        int smallerBranchChildrenSize = n.children.collect { branch -> branch.children.size() }.min()
        def items = new HashMap<Integer, StringBuilder>()
        n.children.each { branch ->
            branch.children.eachWithIndex { levelOneChild, int idx ->
                if (idx < smallerBranchChildrenSize) {
                    if (!items[idx]) {
                        items[idx] = new StringBuilder()
                        items[idx] << bullet
                        items[idx] << ' '
                    }
                    items[idx] << getEachFirstChildsContent(levelOneChild)  // skip levelOneChild, which is a numbering
                }
            }
        }
        if (items.size() == 0) return '<!-- grandchildren are missing -->'
        def result = items.values().join("<br />${nl}")
        return "<p>${nl}${result}${nl}</p>".toString()
    }

    static String getEachFirstChildsContent(n, String sep = ' ', Boolean canSkipLeafCheck = false) {
        /* canSkipLeafCheck for top-level nodes of mkSomething */
        def child = getFirstChildIfNotIgnoreNode(n, canSkipLeafCheck)
        if (child) {
            def grandchildsContent = getEachFirstChildsContent(child, sep)
            def sepGrandchildsContent = grandchildsContent != '' ? "${sep}${grandchildsContent}" : ''
            return "${child.note ?: child.transformedText}${sepGrandchildsContent}".toString()
        } else
            return ''
    }

    static String mkQuote(FPN n) {
        def nl = getNewLine(n)
        def result = new StringBuilder()
        result << "<blockquote>${nl}"
        n.children.each { FPN child ->
            if (!hasIcon(child, icon.noEntry))
                result << "${mkNode(child)}${nl}"
        }
        result << "</blockquote>"
        return result.toString()
    }

    static String mkLink(FPN n) {
        def nl = getNewLine(n)
        for (child in n.children.find { it.link }) {
            return """<a href="${child.link.text}">${nl}${child.text}${nl}</a>""".toString()
        }
        return '<!-- a child with a link is missing -->'
    }

    static String getUuid(FPN n) {
        // e.g. '649ac91e-33a1-476c-93d2-a30170e197a3'
        def uuid = 'UUID'
        def canGenerate = false
        if (!n[uuid])
            canGenerate = true
        else if (!n[uuid].text.endsWith(n.id))
            canGenerate = true
        if (canGenerate)
            n[uuid] = "${UUID.randomUUID().toString()}-${n.id}"
        return n[uuid].text[0..35]
    }

    static String mkExpand(FPN n) {
        return _execIfChildren(n, {
            _mkMacroRich(n, 'expand', [title: n.details ? n.details.text : 'Click here to expand...'])
        })
    }

    static String mkDiv(FPN n) {
        return _execIfChildren(n, {
            Map<String, String> params = n.details ? [class: n.details.text] : null
            return _mkMacroRich(n, 'div', params)
        })
    }

    static String mkCode(FPN n) {
        for (child in n.children.find { FPN it -> it.note }) {
            String lang = child.details ?: 'none'
            String cdata = child.note
            return _mkMacroPlain(n, 'code', cdata, [language: lang])
        }
        return '<!-- a child with a note is missing -->'
    }

    static String _mkMacroPlain(FPN n, String macro, String cdata, Map<String, String> parameters = null) {
        def nl = getNewLine(n)
        def result = new StringBuilder()
        result << """<ac:structured-macro ac:name="${macro}" ac:schema-version="1" ac:macro-id="${getUuid(n)}">${nl}"""
        if (parameters)
            parameters.each {
                result << """<ac:parameter ac:name="${it.key}">${it.value}</ac:parameter>${nl}"""
            }
        if (cdata)
            result << """<ac:plain-text-body>${nl}<![CDATA[${cdata}]]${nl}</ac:plain-text-body>${nl}"""
        result << """</ac:structured-macro>"""
        return result.toString()
    }

    static String _mkMacroRich(FPN n, String macro, Map<String, String> parameters = null, String body = null) {
        def nl = getNewLine(n)
        def result = new StringBuilder()
        def _body = body ?: _mkParent(n)
        result << """<ac:structured-macro ac:name="${macro}" ac:schema-version="1" ac:macro-id="${getUuid(n)}">${nl}"""
        if (parameters)
            parameters.each {
                result << """<ac:parameter ac:name="${it.key}">${it.value}</ac:parameter>${nl}"""
            }
        result << """<ac:rich-text-body>${nl}${_body}${nl}</ac:rich-text-body>${nl}"""
        result << """</ac:structured-macro>"""
        return result.toString()
    }

    static String mkDivExpand(FPN n) {
        return _execIfChildren(n, {
            def title = n.details ? n.details.text : 'Click here to expand...'
            return _mkMacroRich(n, 'div', [class: n.link.text ?: 'expand-in-a-box'], _mkMacroRich(n, 'expand', [title: title]))
        })
    }

    static String mkAttachments(FPN n) {
        return """<ac:structured-macro ac:name="attachments" ac:schema-version="1" ac:macro-id="${getUuid(n)}" />"""
    }

    static String mkStyleImport(FPN n) {
        for (child in n.children.find { FPN it -> it.text }) {
            return _mkMacroPlain(n, 'style', null, [import: child.text])
        }
        return '<!-- a child with text is missing -->'
    }

    static String mkStyle(FPN n) {
        return _execIfChildren(n, {
            return _mkMacroPlain(n, 'style', _mkParent(n))
        })
    }

    static String mkHtml(FPN n) {
        return _execIfChildren(n, {
            return _mkMacroPlain(n, 'html', _mkParent(n))
        })
    }

    static String mkImage(FPN n) {
        for (child in n.children.find { FPN it -> it.text }) {
            def result = new StringBuilder()
            def height = child.details ? /ac:height="${child.details}" / : ''
            def border = hasIcon(child, icon.border_unchecked) ? /ac:border="true" / : ''
            result << """<ac:image ${height}${border}>"""
            result << """<ri:attachment ri:filename="${child.text}" />"""
            result << """</ac:image>"""
            return result.toString()
        }
        return '<!-- a child with text is missing -->'
    }

    static String mkCollector(FPN n) {
        if (n.children.size() > 0) {
            def collectorSep = 'collectorSep'
            def sep = n[collectorSep] ? n[collectorSep].text : ', '
            return n.children.findAll { !hasIcon(it, icon.noEntry) }.collect { child ->
                def grandchildrensContent = getEachFirstChildsContent(child, sep)
                def sepGrandchildsContent = grandchildrensContent != '' ? "${sep}${grandchildrensContent}" : ''
                if (grandchildrensContent)
                    return "${child.note ?: child.transformedText}${sepGrandchildsContent}"
                else
                    return child.note ?: child.transformedText
            }.join(sep)
        } else
            return '<!-- a child is missing -->'
    }
}
