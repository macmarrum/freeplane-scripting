/*
 * Inspired by https://github.com/EdoFro/Freeplane_MarkdownHelper
 */


import groovy.xml.XmlUtil
import org.freeplane.api.Node
import org.freeplane.api.Node as FN
import org.freeplane.plugin.script.proxy.ScriptUtils

import java.text.MessageFormat

class ConfluenceStorage {

    static c = ScriptUtils.c()

    static style = [
            cStorageMarkupRoot : 'cStorageMarkupRoot',
            cStorageMarkupMaker: 'cStorageMarkupMaker',
    ]

    static icon = [
            noEntry                      : 'emoji-26D4',
            eol_chequeredFlag            : 'emoji-1F3C1',
            noSep_cancer                 : 'emoji-264B',
            pButton                      : 'emoji-1F17F',
            nl_rightArrowCurvingDown     : 'emoji-2935',
            ol_keycapHash                : 'emoji-0023-20E3',
            border_unchecked             : 'unchecked',
            nbsp_gemini                  : 'emoji-264A',
            numbers_inputNumbers         : 'emoji-1F522',
            collapse_fastUpButton        : 'emoji-23EB',
            xmlEscape_broom              : 'emoji-1F9F9',
            pReplacements_doubleCurlyLoop: 'emoji-27BF',
            stopAtThis_stopSign          : 'emoji-1F6D1',
            noSepAfter_lastQuarterMoon   : 'emoji-1F317',
    ]

    static tbl = [
            rowCnt: '₵',
            rowNum: '№',
    ]

    static Boolean isMarkupRoot(FN n) {
        return n.style.name == style.cStorageMarkupRoot
    }

    static Boolean isMarkupMaker(FN n) {
        return n.style.name == style.cStorageMarkupMaker
    }

    static Boolean hasIcon(FN n, String icon) {
        return n.icons.icons.contains(icon)
    }

    static Boolean hasIcon(FN n, List<String> icons) {
        return n.icons.icons.any { it in icons }
    }

    /**
     * makeMarkup if it's a markup maker, otherwise
     * First escape xml, if broom present
     * Only then do pReplacements if doubleCurlyLoop present
     * and replace each space with nbsp, if gemini present
     */
    static String getContent(FN n) {
        String content = isMarkupMaker(n) ? makeMarkup(n) : n.note?.text ?: n.transformedText
        if (hasIcon(n, icon.xmlEscape_broom)) content = XmlUtil.escapeXml(content)
        if (hasIcon(n, icon.pReplacements_doubleCurlyLoop)) content = _applyReplacements(n, content)
        return hasIcon(n, icon.nbsp_gemini) ? content.replaceAll(/ /, '&nbsp;') : content
    }

    static String makeMarkup(FN n) {
        return switch (n.text) {
            case 'parent' -> mkParent(n)
            case 'table' -> mkTable(n)
            case 'list' -> mkList(n)
            case 'zip-list' -> mkZipList(n)
            case 'quote' -> mkQuote(n)
            case 'link' -> mkLink(n)
            case 'expand' -> mkExpand(n)
            case 'div' -> mkDiv(n)
            case 'code' -> mkCode(n)
            case 'page-info' -> mkPageInfo(n)
            case 'div-expand' -> mkDivExpand(n)
            case 'attachments' -> mkAttachments(n)
            case 'style-import' -> mkStyleImport(n)
            case 'style' -> mkStyle(n)
            case 'html' -> mkHtml(n)
            case 'image' -> mkImage(n)
            case 'csv' -> mkCsv(n)
            case 'wiki' -> mkWiki(n)
            case 'markdown' -> mkMarkdown(n)
            case 'section' -> mkSection(n)
            case 'column' -> mkColumn(n)
            case 'template' -> mkTemplate(n)
            case 'format' -> mkFormat(n)
            default -> "<!-- mk function by that name not found: ${n.text} -->"
        }
    }

    static noSepIcons = [icon.noSepAfter_lastQuarterMoon, icon.noSep_cancer]

    static String getSpaceSep(FN n) {
        return hasIcon(n, noSepIcons) ? '' : ' '
    }

    static String getNewLine(FN n) {
        return hasIcon(n, icon.nl_rightArrowCurvingDown) ? '\n' : ''
    }

    static String getEol(FN n) {
        return hasIcon(n, icon.eol_chequeredFlag) ? '\n' : ''
    }

    static String mkParent(FN n) {
        return _execIfChildren(n, { _mkParent(n) })
    }

    static String _execIfChildren(FN n, Closure closure) {
        if (n.children.size() > 0)
            return closure()
        else
            return '<!-- children are missing -->'
    }

    static String _mkParent(FN n) {
        def sb = new StringBuilder()
        n.children.each { sb << mkNode(it) }
        return sb.toString()
    }

    static LinkedHashMap<String, String> pReplacements = [
            /(\n| )-> /                                    : '$1→ ',
            /(\n| )=> /                                    : '$1&rArr; ',
            /(\n| )>> /                                    : '\n&#8611; ', // >->
            /\n=- /                                        : '\n→ ',
            /\n== /                                        : '\n&#8611; ', // >->
            /\n-- /                                        : '\n&ndash; ',
            /\n--- /                                       : '\n&mdash; ',
            /([^-])?---([^-])?/                            : '$1&mdash;$2',
            /([^-])?--([^-])?/                             : '$1&ndash;$2',
            /\{\{([^{]+)\}\}/                              : '<code>$1</code>',
            /(?<=^|[^a-zA-Z0-9])\*(?! )(.+?)(?<! )\*(?=[^a-zA-Z0-9]|$)/: '<b>$1</b>',
            /(?<=^|[^a-zA-Z0-9])_(?! )(.+?)(?<! )_(?=[^a-zA-Z0-9]|$)/  : '<i>$1</i>',
            /(?<=^|[^a-zA-Z0-9])-(?! )(.+?)(?<! )-(?=[^a-zA-Z0-9]|$)/  : '<s>$1</s>',
            /(?<=^|[^a-zA-Z0-9])\+(?! )(.+?)(?<! )\+(?=[^a-zA-Z0-9]|$)/: '<u>$1</u>',
    ]

    static String _applyReplacements(FN n, String content) {
        content = content.replaceAll(/\n\+ /, "\n${getSimBullet(n)} ")
        pReplacements.each { content = content.replaceAll(it.key, it.value) }
        return content.replaceAll(/\n/, "<br />${getNewLine(n)}")
    }

    static StringBuilder mkNode(FN n) {
        def result = new StringBuilder()
        if (hasIcon(n, icon.noEntry)) {
            return result
        } else {
            def eol = getEol(n)
            def nl = getNewLine(n)
            def sep = getSpaceSep(n)
            def isP = hasIcon(n, icon.pButton)
            if (isMarkupMaker(n)) {
                if (isP) {
                    result << '<p>' << nl << makeMarkup(n) << '</p>' << eol
                    return result
                } else {
                    result << makeMarkup(n) << sep << eol
                    return result
                }
            } else {
                if (_isHeading(n)) {
                    result << _mkHeading(n, nl) << eol
                    return result
                } else {
                    String pContent
                    if (isP && !hasIcon(n, icon.pReplacements_doubleCurlyLoop)) // avoid double replacements
                        pContent = _applyReplacements(n, getContent(n))
                    else
                        pContent = getContent(n)
                    if (isP)
                        result << '<p>'
                    result << pContent << sep << eol
                    if (!hasIcon(n, icon.stopAtThis_stopSign))
                        n.children.each { result << mkNode(it) }
                    if (isP)
                        result << '</p>'
                    return result
                }
            }
        }
    }

    static Boolean _isHeading(FN n) {
        return (n.icons.size() > 0 && n.icons.icons.any { it.startsWith('full-') })
    }

    static StringBuilder _mkHeading(FN n, String nl) {
        def hIcon = n.icons.icons.find { it.startsWith('full-') }
        def hLevel = hIcon[5..-1]
        def childrenBody = n.children.size() > 0 ? n.children.collect { mkNode(it) }.join('') : ''
        def result = new StringBuilder()
        result << '<h' << hLevel << '>' << nl << getContent(n) << nl << '</h' << hLevel << '>' << childrenBody
        return result
    }


    /**
     * canExcludeMarkupMaker for getFirstChildChain, used by mkCsv – to stop at markupMaker nodes
     */
    static FN getFirstChildIfNotIgnoreNode(FN n, boolean canExcludeMarkupMaker = true) {
        def isMarkupMakerAndCanExcludeIt = isMarkupMaker(n) && canExcludeMarkupMaker
        if (isMarkupMakerAndCanExcludeIt || n.children.size() == 0 || hasIcon(n, icon.stopAtThis_stopSign) || hasIcon(n.children[0], icon.noEntry))
            return null
        else
            return n.children[0]
    }

    static int _tbl_countFirstChildChain(FN n, int cnt = 0) {
        def child = getFirstChildIfNotIgnoreNode(n, false)
        if (child)
            return _tbl_countFirstChildChain(child, ++cnt)
        else
            return cnt
    }

    static StringBuilder mkTable(FN n) {
        def tableAnnotateText = getTableAnnotateText(n)
        def canAnnotate = tableAnnotateText == annotate
        def canClearAnnotations = tableAnnotateText == clear
        def nl = getNewLine(n)
        HiLite1st hiLite1st
        if (n['hiLite1st']) {
            hiLite1st = HiLite1st.valueOf(n['hiLite1st'].text.toUpperCase())
        } else {
            hiLite1st = HiLite1st.NONE
        }
        def tableWiki = new StringBuilder()
        def colNum = 1
        def rowNum = 1
        tableWiki << '<table>' << nl << '<colgroup><col /><col /></colgroup>' << nl << '<tbody>' << nl
        // clean up details containing old tbl.rowCnt or tbl.rowNum
        if (canAnnotate || canClearAnnotations)
            n.findAll().drop(1).each { Node it -> if (it.detailsText && (it.details.text.startsWith(tbl.rowCnt) || it.details.text.startsWith(tbl.rowNum))) it.details = null }
        // the first column in each row is technical, therefore it's skipped
        n.children.each { FN row ->
            if (!hasIcon(row, icon.noEntry)) {  // not ignoreNode
                if (row.children.size() > 1) {  // each child (with descendants) is a column (vertical layout)
                    tableWiki << '<tr>' << nl
                    makeTableCellOfEachChildWithDescendantsAndAppendTo(tableWiki, row.children, rowNum, colNum, hiLite1st, nl, canAnnotate)
                    tableWiki << '</tr>' << nl // close the row
                } else {  // each first-child is a column (horizontal layout)
                    final firstChildChainSize = _tbl_countFirstChildChain(row)
                    if (canAnnotate)
                        row.details = (new StringBuilder() << tbl.rowCnt << firstChildChainSize).toString()
                    if (firstChildChainSize > 0) {
                        tableWiki << '<tr>' << nl
                        mkTableCellOfEachFirstChildInChainAndAppendTo(tableWiki, row.children[0], rowNum, colNum, hiLite1st, nl, canAnnotate)
                        tableWiki << '</tr>' << nl // close the row
                    }
                }
                rowNum++
            }
        }
        tableWiki << '</tbody>' << nl << '</table>'
        return tableWiki
    }

    enum HiLite1st {
        ROW, COLUMN, NONE
    }

    static void makeTableCellOfEachChildWithDescendantsAndAppendTo(StringBuilder tableWiki, List<FN> children, int rowNum, int colNum, HiLite1st hiLite1st, String nl, boolean canAnnotate) {
        children.each { FN child ->
            if (!hasIcon(child, icon.noEntry)) {
                tableWiki << makeTableCell(this::mkNode, child, rowNum, colNum, hiLite1st, nl, canAnnotate)
                colNum++
            }
        }
    }

    static void mkTableCellOfEachFirstChildInChainAndAppendTo(StringBuilder tableWiki, FN n, int rowNum, int colNum, HiLite1st hiLite1st, String nl, boolean canAnnotate) {
        tableWiki << makeTableCell(this::getContent, n, rowNum, colNum, hiLite1st, nl, canAnnotate)
        // canExcludeMarkupMaker=false because each cell is basically a top-level node, i.e. can be a cStorageMarkupMaker
        FN firstChildIfNotIgnoreNode = getFirstChildIfNotIgnoreNode(n, false)
        if (firstChildIfNotIgnoreNode) {
            colNum++
            mkTableCellOfEachFirstChildInChainAndAppendTo(tableWiki, firstChildIfNotIgnoreNode, rowNum, colNum, hiLite1st, nl, canAnnotate)
        }
    }

    static StringBuilder makeTableCell(Closure method, FN n, int rowNum, int colNum, HiLite1st hiLite1st, String nl, boolean canAnnotate) {
        def result = new StringBuilder()
        if (canAnnotate)
            n.details = (new StringBuilder() << tbl.rowNum << colNum).toString()
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
        result << method(n)  // e.g. getContent or mkNode
        result << '</' << tag[1..-1] << nl
        return result
    }

    static final String clear = 'clear'
    static final String annotate = 'annotate'
    static final String none = 'none'
    static final ArrayList<String> clear_annotate_none = [clear, annotate, none]

    static String getTableAnnotateText(FN n) {
        def detailsText = n.details?.text
        if (detailsText in clear_annotate_none)
            return detailsText
        def markupRoot = n.pathToRoot.find { isMarkupRoot(it) }
        def markupRootDetailsText = markupRoot.details?.text
        if (markupRootDetailsText in clear_annotate_none)
            return markupRootDetailsText
    }

    static StringBuilder mkList(FN n) {
        def nl = getNewLine(n)
        def result = new StringBuilder()
        def tag = hasIcon(n, icon.ol_keycapHash) ? '<ol>' : '<ul>'
        result << tag << nl
        String body
        n.children.each {
            body = mkNode(it)
            if (body.trim().size() > 0)
                result << '<li>' << nl << body << nl << '</li>' << nl
        }
        result << '</' << tag[1..-1]
        return result
    }

    /**
     * mkZipList, like mkCsv, uses its own content gatherer, instead of mkNode, to consider only the first child on each level
     *  TODO: consider if mkNode could be used instead, especially now that <div> is used in place of <p>
     *      -- benefits / disadvantages
     */
    static StringBuilder mkZipList(FN n) {
        def nl = getNewLine(n)
        def bullet = getSimBullet(n)
        int smallerBranchChildrenSize = n.children.collect { branch -> branch.children.size() }.min()
        def items = new LinkedHashMap<Integer, StringBuilder>()
        n.children.each { branch ->
            branch.children.eachWithIndex { levelOneChild, int idx ->
                if (idx < smallerBranchChildrenSize) {
                    if (!items[idx]) {
                        items[idx] = new StringBuilder() << bullet << ' '
                    }
                    items[idx] << getEachFirstChildsContent(levelOneChild, ' ', true)  // skip levelOneChild, which is a numbering
                }
            }
        }
        final itemsSize = items.size()
        final sb = new StringBuilder()
        if (itemsSize == 0) {
            sb << '<!-- grandchildren are missing -->'
            return sb
        }
        sb << '<div>' << nl
        items.values().eachWithIndex { item, i ->
            sb << item
            if (i < itemsSize - 1)
                sb << '<br />' << nl
        }
        sb << nl << '</div>'
        return sb
    }

    static String getSimBullet(Node n) {
        return n['simBullet'] ? n['simBullet'].text : '●'
    }

    /**
     * canExcludeMarkupMaker for getFirstChildIfNotIgnoreNode
     * then for getFirstChildChain, used by mkCsv – to stop at markupMaker nodes
     */
    static StringBuilder getEachFirstChildsContent(n, String sep = ' ', boolean canExcludeMarkupMaker = true) {
        /* canExcludeMarkupMaker for top-level nodes of mkSomething */
        def child = getFirstChildIfNotIgnoreNode(n, canExcludeMarkupMaker)
        final sb = new StringBuilder()
        if (child) {
            sb << getContent(child)
            if (!hasIcon(child, noSepIcons))
                sb << sep
            final grandchildsContent = getEachFirstChildsContent(child, sep)
            if (!grandchildsContent.isBlank())
                sb << grandchildsContent
        }
        return sb
    }

    static List<FN> getFirstChildChain(FN n, List<FN> firstChildChain = null) {
        if (firstChildChain.is(null))
            firstChildChain = new LinkedList<FN>()
        firstChildChain.add(n)
        def child = getFirstChildIfNotIgnoreNode(n)
        if (child)
            return getFirstChildChain(child, firstChildChain)
        else
            return firstChildChain
    }

    static String mkQuote(FN n) {
        def nl = getNewLine(n)
        def result = new StringBuilder()
        result << '<blockquote>' << nl << mkParent(n) << nl << '</blockquote>'
        return result.toString()
    }

    static String mkLink(FN n) {
        /* make link of the first child with a link */
        def nl = getNewLine(n)
        for (child in n.children.find { it.link }) {
            def result = new StringBuilder()
            result << '<a href="' << XmlUtil.escapeXml(child.link.text) << '">' << nl << getContent(child) << nl << '</a>'
            return result.toString()
        }
        return '<!-- a child with a link is missing -->'
    }

    static String getUuid(FN n) {
        // e.g. '649ac91e-33a1-476c-93d2-a30170e197a3'
        def uuid = 'UUID'
        def canGenerate = false
        if (!n[uuid])
            canGenerate = true
        else if (!n[uuid].text.endsWith(n.id))
            canGenerate = true
        if (canGenerate) {
            def uuidValue = UUID.randomUUID().toString()
            def result = new StringBuilder()
            result << uuidValue << '-' << n.id
            n[uuid] = result.toString()
            return uuidValue
        } else {
            return n[uuid].text[0..35]
        }
    }

    static String mkExpand(FN n) {
        return _execIfChildren(n, {
            _mkMacroRich(n, 'expand', [title: n.details?.text ?: 'Click here to expand...'])
        })
    }

    static String mkDiv(FN n) {
        return _execIfChildren(n, {
            Map<String, String> params = n.detailsText ? [class: n.details.text] : null
            return _mkMacroRich(n, 'div', params)
        })
    }

    static String mkSection(FN n) {
        return _execIfChildren(n, {
            Map<String, String> params = n.icons.contains(icon.border_unchecked) ? [border: 'true'] : null
            return _mkMacroRich(n, 'section', params)
        })
    }

    static String mkColumn(FN n) {
        return _execIfChildren(n, {
            Map<String, String> params = n.detailsText ? [width: n.details.text] : null
            return _mkMacroRich(n, 'column', params)
        })
    }

    static String mkCode(FN n) {
        // https://confluence.atlassian.com/doc/code-block-macro-139390.html
        // final LANGUAGES = ['ActionScript', 'AppleScript', 'Bash', 'C#', 'C++', 'CSS', 'ColdFusion', 'Delphi', 'Diff', 'Erlang', 'Groovy', 'HTML and XML', 'Java', 'Java FX', 'JavaScript', 'PHP', 'Plain Text', 'PowerShell', 'Python', 'Ruby', 'SQL', 'Sass', 'Scala', 'Visual Basic', 'YAML']
        final MAX_LANG_SIZE = 12
        for (child in n.children.find { FN it -> it.children.size() > 0 || it.noteText !== null || it.detailsText !== null }) {
            String lang
            String cdata  // get cdata from text, alternatively from children or from note
            String text = child.text
            String title
            if (text.size() > MAX_LANG_SIZE) { // text is not a language name
                lang = child.details?.text ?: 'none'
                cdata = text
                if (n.detailsText) title = n.details.text
            } else {
                lang = child.text ?: 'none'
                if (child.children.size() > 0)
                    cdata = mkParent(child)
                else
                    cdata = child.plainNote  // no formula evaluation (unexposed method)
            if (child.detailsText) title = child.details.text
            }
            def params = [language: lang]
            if (title) params.title = title
            if (hasIcon(child, icon.collapse_fastUpButton)) params.collapse = 'true'
            if (hasIcon(child, icon.numbers_inputNumbers)) params.linenumbers = 'true'
            if (child.link.text) params.theme = child.link.text
            return _mkMacroPlain(n, 'code', cdata, params)
        }
        return '<!-- a child with children or a note is missing -->'
    }

    static String mkPageInfo(FN n) {
        String infoType = n.detailsText ? n.details.text : 'Title'
        return _mkPageInfo(n, infoType)
    }

    static String _mkPageInfo(FN n, String infoType, String type = 'Flat') {
        /* Page id, Current version, Tiny url, Title, ...  */
        return _mkMacroPlain(n, 'page-info', null, [infoType: infoType, type: type])
    }

    static String _mkMacroPlain(FN n, String macro, String cdata, Map<String, String> parameters = null) {
        def nl = getNewLine(n)
        def result = new StringBuilder()
        result << '<ac:structured-macro ac:name="' << macro << '" ac:schema-version="1" ac:macro-id="' << getUuid(n) << '">' << nl
        if (parameters)
            parameters.each {
                result << '<ac:parameter ac:name="' << it.key << '">' << it.value << '</ac:parameter>' << nl
            }
        if (cdata)
            result << '<ac:plain-text-body>' << nl << '<![CDATA[' << cdata << ']]>' << nl << '</ac:plain-text-body>' << nl
        result << '</ac:structured-macro>'
        return result.toString()
    }

    static String _mkMacroRich(FN n, String macro, Map<String, String> parameters = null, String body = null) {
        def nl = getNewLine(n)
        def result = new StringBuilder()
        def _body = body ?: _mkParent(n)
        result << '<ac:structured-macro ac:name="' << macro << '" ac:schema-version="1" ac:macro-id="' << getUuid(n) << '">' << nl
        if (parameters)
            parameters.each {
                result << '<ac:parameter ac:name="' << it.key << '">' << it.value << '</ac:parameter>' << nl
            }
        result << '<ac:rich-text-body>' << nl << _body << nl << '</ac:rich-text-body>' << nl
        result << '</ac:structured-macro>'
        return result.toString()
    }

    static String mkDivExpand(FN n) {
        return _execIfChildren(n, {
            def nl = getNewLine(n)
            def title = n.detailsText ? n.details.text : 'Click here to expand...'
            def className = n.link.text ?: 'expand-in-a-box'
            def result = new StringBuilder()
            result << '<div class="' << className << '">' << nl
            result << _mkMacroRich(n, 'expand', [title: title]) << nl
            result << '</div>'
            return result.toString()
        })
    }

    static String mkAttachments(FN n) {
        def nl = getNewLine(n)
        def canUpload = n['attachmentsUpload'].num0 == 1 ? 'true' : 'false'
        def canOld = n['attachmentsOld'].num0 == 1 ? 'true' : 'false'
        def result = new StringBuilder()
        result << '<ac:structured-macro ac:name="attachments" ac:schema-version="1" ac:macro-id="' << getUuid(n) << '">' << nl
        result << '<ac:parameter ac:name="upload">' << canUpload << '</ac:parameter>' << nl
        result << '<ac:parameter ac:name="old">' << canOld << '</ac:parameter>' << nl << '</ac:structured-macro>'
        return result.toString()
    }

    static String mkStyleImport(FN n) {
        for (child in n.children.find { FN it -> it.text }) {
            return _mkMacroPlain(n, 'style', null, [import: child.text])
        }
        return '<!-- a child with text is missing -->'
    }

    static String mkStyle(FN n) {
        return _execIfChildren(n, {
            return _mkMacroPlain(n, 'style', _mkParent(n))
        })
    }

    static String mkHtml(FN n) {
        return _execIfChildren(n, {
            return _mkMacroPlain(n, 'html', _mkParent(n))
        })
    }

    static String mkImage(FN n) {
        for (child in n.children.find { FN it -> it.text }) {
            def result = new StringBuilder()
            result << '<ac:image '
            if (child.detailsText)
                result << 'ac:height="' << child.details.text << '" '
            if (hasIcon(child, icon.border_unchecked))
                result << 'ac:border="true" '
            result << '>'
            result << '<ri:attachment ri:filename="' << child.text << '" />'
            result << '</ac:image>'
            return result.toString()
        }
        return '<!-- a child with text is missing -->'
    }

    /**
     * mkCsv is like mkParent but using getContent instead of mkNode (no headings or paragraphs)
     * + collects only the first child of each node
     * + uses a comma or any string as the separator
     */
    static StringBuilder mkCsv(FN n) {
        // sep can be defined as the attribute csvSep
        // sep can be defined in details
        // for table cells (details start with №), the default sep is used
        def sb = new StringBuilder()
        if (n.children.size() > 0) {
            String sep
            final defaultSep = ', '
            final csvSep = 'csvSep'
            if (n[csvSep].text !== null)
                sep = n[csvSep].text
            else if (n.detailsText !== null)
                sep = n.details.text.startsWith(tbl.rowNum) ? defaultSep : n.details.text
            else
                sep = defaultSep
            def cells = new LinkedList<FN>()
            n.children.each { Node it -> if (!hasIcon(it, icon.noEntry)) cells.addAll(getFirstChildChain(it)) }
            def cellsSize = cells.size()
            int i
            cells.each { Node it ->
                sb << getContent(it)
                if (++i < cellsSize) { // not the last element
                    if (!hasIcon(it, noSepIcons)) // noSepAfter is missing
                        sb << sep
                } else // last item – space if noSepAfter is missing
                    sb << getSpaceSep(it)
            }
            // NB. getContent->makeMarkup->mk***->mkNode adds a trailing space (unless noSepAfter)
            return sb
        } else
            return sb << '<!-- a child is missing -->'
    }

    static String mkWiki(FN n) {
        return _execIfChildren(n, {
            return _mkMacroPlain(n, 'unmigrated-wiki-markup', _mkParent(n))
        })
    }

    static String mkMarkdown(FN n) {
        return _execIfChildren(n, {
            return _mkMacroPlain(n, 'markdown', _mkParent(n))
        })
    }

    enum TemplateType {
        MESSAGE, STRING;
    }

    static String mkTemplate(FN n) {
        return _mkTemplate(n, TemplateType.MESSAGE)
    }

    static String mkFormat(FN n) {
        return _mkTemplate(n, TemplateType.STRING)
    }

    static String _mkTemplate(FN n, TemplateType templateType) {
        def yesentryChildren = n.children.findAll { !hasIcon(it, icon.noEntry) }
        if (yesentryChildren.size() == 0)
            return '<!-- a child (pattern) is missing -->'
        else {
            def patternNode = yesentryChildren[0]
            def yesentryPatternChildren = patternNode.children.findAll { !hasIcon(it, icon.noEntry) }
            if (yesentryPatternChildren.size() == 0)
                return '<!-- a (yes-entry) child is missing -->'
            else {
                def firstChildChain = yesentryPatternChildren.collect { getFirstChildChain(it) }.flatten()
                def contentList = firstChildChain.collect { getContent(it) }
                def pattern = patternNode.details?.text ?: patternNode.note?.text ?: getContent(patternNode)
                if (templateType == TemplateType.MESSAGE)
                    return MessageFormat.format(pattern, *contentList)
                else if (templateType == TemplateType.STRING)
                    return String.format(pattern, *contentList)
            }
        }
    }

    static FN createMarkupMaker(FN node, String name) {
        def maker = node.createChild(name)
        maker.style.name = style.cStorageMarkupMaker
        c.select(maker)
        return maker
    }

    static createCode(FN node, String language = 'sql', String title = null, boolean showLineNumbers = true, String theme = 'Eclipse', boolean collapse = false) {
        def n = createMarkupMaker(node, 'code')
        if (title)
            n.details = title
        n.icons.add(icon.nl_rightArrowCurvingDown)
        def code = n.createChild('SELECT *\nFROM DUAL\nWHERE 1=1')
        code.details = language
        if (showLineNumbers)
            code.icons.add(icon.numbers_inputNumbers)
        if (collapse)
            code.icons.add(icon.collapse_fastUpButton)
        code.link.text = theme
        c.select(code)
    }

    static createDivExpandCode(FN node, String details = 'Expand: SQL Statement', String cssClassName = 'macmarrum-expand', String language ='sql', boolean showLineNumbers = true, String theme = 'Eclipse') {
        def n = createMarkupMaker(node, 'div-expand')
        n.details = details
        n.link.text = cssClassName
        n.icons.add(icon.nl_rightArrowCurvingDown)
        createCode(n, language, null, showLineNumbers, theme)
    }

    static createList(FN node) {
        def n = createMarkupMaker(node, 'list')
        n.icons.add(icon.nl_rightArrowCurvingDown)
    }

    static createLink(FN node) {
        createMarkupMaker(node, 'link')
    }

    static createFormat(FN node, String pattern = '%s') {
        def n = createMarkupMaker(node, 'format')
        def p = n.createChild(pattern)
        c.select(p)
    }

    static createTable(FN node, String styleName = '=Numbering#') {
        def n = createMarkupMaker(node, 'table')
        n.icons.add(icon.nl_rightArrowCurvingDown)
        def elem = n.createChild()
        elem.style.name = styleName
        c.select(elem)
    }

    static createParent(FN node) {
        createMarkupMaker(node, 'parent')
    }

    static createCsv(FN node) {
        createMarkupMaker(node, 'csv')
    }
}
