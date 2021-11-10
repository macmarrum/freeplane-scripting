/*
 * Inspired by https://github.com/EdoFro/Freeplane_MarkdownHelper
 */
import org.freeplane.api.Node as FPN

class ConfluenceStorage {

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
            nbsp_gemini             : 'emoji-264A',
            numbers_inputNumbers    : 'emoji-1F522',
            collapse_fastUpButton   : 'emoji-23EB',
    ]

    static HashMap<String, String> tbl = [
            rowCnt: '₵',
            rowNum: '№',
    ]

    static Boolean isLeaf(FPN n) {
        return n.style.name == style.leaf
    }

    static Boolean hasIcon(FPN n, String icon) {
        return n.icons.icons.contains(icon)
    }

    static String getContent(FPN n) {
        def content = n.note ? n.note.text : n.transformedText
        return hasIcon(n, icon.nbsp_gemini) ? content.replaceAll(/ /, '&nbsp;') : content
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
                if (_isHeading(n)) {
                    return _mkHeading(n, nl, eol)
                } else {
                    def body = "${getContent(n)}${getSep(n)}${n.children.collect { mkNode(it) }.join('')}"
                    if (hasIcon(n, icon.pButton)) {
                        return "<p>${nl}${body.replaceAll(/\n/, "<br />${nl}")}${nl}</p>${eol}".toString()
                    } else {
                        return "${body}${eol}".toString()
                    }
                }
            }
        }
    }

    static Boolean _isHeading(FPN n) {
        return (n.icons.size() > 0 && n.icons.icons.any { it.startsWith('full-') })
    }

    static String _mkHeading(FPN n, String nl, String eol) {
        def hIcon = n.icons.icons.find { it.startsWith('full-') }
        def hLevel = hIcon[5..-1]
        def childrenBody = n.children.size() > 0 ? n.children.collect { mkNode(it) }.join('') : ''
        return "<h${hLevel}>${nl}${getContent(n)}${nl}</h${hLevel}>${childrenBody}${eol}".toString()
    }


    static FPN getFirstChildIfNotIgnoreNode(FPN n, Boolean canSkipLeafCheck = false) {
        def isLeafAndNotSkipped = isLeaf(n) && !canSkipLeafCheck
        if (isLeafAndNotSkipped || n.children.size() == 0 || hasIcon(n.children[0], icon.noEntry))
            return null
        else
            return n.children[0]
    }

    static int _tbl_countFirstChildChain(FPN n, int cnt = 0) {
        def child = getFirstChildIfNotIgnoreNode(n, true)
        if (child)
            return _tbl_countFirstChildChain(child, ++cnt)
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
        // clean up details containing old tbl.rowNum
        n.findAll().drop(1).each { if (it.details && it.details.text.startsWith(tbl.rowNum)) it.details = null }
        // the first column in each row is technical, therefore it's skipped
        n.children.each { FPN row ->
            if (!hasIcon(row, icon.noEntry)) {  // not ignoreNode
                row.details = "${tbl.rowCnt}${_tbl_countFirstChildChain(row)}".toString()

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
        n.details = "${tbl.rowNum}${colNum}"
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
        // canSkipLeafCheck=true because each cell is basically a top-level node, i.e. can be a leaf
        FPN firstChildIfNotIgnoreNode = getFirstChildIfNotIgnoreNode(n, true)
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

    static List<FPN> getFirstChildChain(FPN n, List<FPN> firstChildChain = null) {
        if (firstChildChain.is(null))
            firstChildChain = new ArrayList<>()
        firstChildChain.add(n)
        def child = getFirstChildIfNotIgnoreNode(n)
        if (child)
            return getFirstChildChain(child, firstChildChain)
        else
            return firstChildChain
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
        /* make link of the first child with a link */
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
            String lang = child.text ?: 'none'
            def params = [language: lang]
            if (child.details) params.title = child.details.text
            if (hasIcon(child, icon.collapse_fastUpButton)) params.collapse = 'true'
            if (hasIcon(child, icon.numbers_inputNumbers)) params.linenumbers = 'true'
            if (child.link.text) params.theme = child.link.text
            String cdata = child.note.text
            return _mkMacroPlain(n, 'code', cdata, params)
        }
        return '<!-- a child with a note is missing -->'
    }

    static String mkPageInfo(FPN n, String infoType, String type = 'Flat') {
        /* Page id, Current version, Tiny url, Title, ...  */
        return _mkMacroPlain(n, 'page-info', null, [infoType: infoType, type: type])
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
            result << """<ac:plain-text-body>${nl}<![CDATA[${cdata}]]>${nl}</ac:plain-text-body>${nl}"""
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
        def nl = getNewLine(n)
        def canUpload = n['attachmentsUpload'].num0 == 1 ? 'true' : 'false'
        def canOld = n['attachmentsOld'].num0 == 1 ? 'true' : 'false'
        return """<ac:structured-macro ac:name="attachments" ac:schema-version="1" ac:macro-id="${getUuid(n)}">${nl}<ac:parameter ac:name="upload">${canUpload}</ac:parameter>${nl}<ac:parameter ac:name="old">${canOld}</ac:parameter>${nl}</ac:structured-macro>"""
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

    static String mkCsv(FPN n) {
        if (n.children.size() > 0) {
            def csvSep = 'csvSep'
            def sep = n[csvSep] ? n[csvSep].text : ', '
            def cells = new ArrayList<FPN>()
            n.children.findAll { !hasIcon(it, icon.noEntry) }.each { cells.addAll(getFirstChildChain(it)) }
            def cellsSize = cells.size()
            int i
            return cells.collect { "${it.note ?: it.transformedText}${++i == cellsSize || hasIcon(it, icon.noSep_cancer) ? '' : sep}" }.join('')
        } else
            return '<!-- a child is missing -->'
    }
}
