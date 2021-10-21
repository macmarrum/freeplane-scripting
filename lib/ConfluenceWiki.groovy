/*
 * Inspired by https://github.com/EdoFro/Freeplane_MarkdownHelper
 */
import org.freeplane.plugin.script.proxy.Proxy.Node as FPN

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
//            noP_prohibited          : 'emoji-1F6AB',
            nl_rightArrowCurvingLeft: 'emoji-21A9',
            ol_keycapHash           : 'emoji-0023-20E3',
    ]

    def static hasIcon(FPN n, String icon) {
        return n.icons.icons.contains(icon)
    }

    def static getContent(FPN n) {
        return n.note ?: n.transformedText
    }

    def static getSep(FPN n) {
        return hasIcon(n, icon.noSep_cancer) ? '' : ' '
    }

    def static getNewLine(FPN n) {
        return hasIcon(n, icon.nl_rightArrowCurvingLeft) ? '\n' : ''
    }

    def static getEol(FPN n) {
        return hasIcon(n, icon.eol_chequeredFlag) ? '\n' : ''
    }

    def static mkNode(FPN n) {
        def eol = getEol(n)
        def nl = getNewLine(n)
        if (!hasIcon(n, icon.noEntry)) {
            if (n.hasStyle(style.leaf)) {
                return "${n.note}${eol}"
            } else {
                def body = "${getContent(n)}${getSep(n)}${n.children.collect { mkNode(it) }.join('')}"
                if (_isHeading(n)) {
                    return _mkHeading(n, body, nl, eol)
                } else if (hasIcon(n, icon.pButton)) {
                    return "<p>${nl}${body}${nl}</p>${eol}"
                } else {
                    return "${body}${eol}"
                }
            }
        }
    }

    def static _isHeading(FPN n) {
        return (n.icons.size() > 0 && n.icons.icons.any { it.startsWith('full-') })
    }

    def static _mkHeading(FPN n, String body, String nl, String eol) {
        def hIcon = n.icons.icons.find { it.startsWith('full-') }
        def hLevel = hIcon[5..-1]
        return "<h${hLevel}>${nl}${body}${nl}</h${hLevel}>${eol}".toString()
    }


    def static getFirstChildIfNotIgnoreNode(n) {
        if (n.hasStyle(style.leaf))  // when a leaf node, children are ignored
            return null
        if (n.children.size() > 0)
            if (!hasIcon(n, icon.noEntry))  // not ignoreNode
                return n.children[0]
    }

    def static countFirstChildChain(n, cnt = 0) {
        def child = getFirstChildIfNotIgnoreNode(n)
        if (child)
            countFirstChildChain(child, ++cnt)
        else
            return cnt
    }

    enum HiLite1st {
        ROW, COLUMN, NONE
    }

    def static mkTable(FPN n) {
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
        tableWiki << "<table>${nl}<colgroup><col/><col/></colgroup>${nl}<tbody>${nl}"
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

    def static mkTableCell(n, int rowNum, int colNum, HiLite1st hiLite1st, String nl) {
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
        def firstChildIfNotIgnoreNode = getFirstChildIfNotIgnoreNode(n)
        if (firstChildIfNotIgnoreNode) {
            result << mkTableCell(firstChildIfNotIgnoreNode, rowNum, ++colNum, hiLite1st, nl)
        }
        return result.toString()
    }

    def static mkList(FPN n) {
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

    def static mkQuote(FPN n) {
        def nl = getNewLine(n)
        def result = new StringBuilder()
        result << "<blockquote>${nl}"
        n.children.each { FPN child ->
            if (!hasIcon(child, icon.noEntry))
                result << "${mkNode(child)}${nl}"
        }
        result << "</blockquote>${getEol(n)}"
        return result.toString()
    }
}