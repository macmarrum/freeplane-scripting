/*
 * Copyright (C) 2022-2024  macmarrum (at) outlook (dot) ie
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
// Inspired by https://github.com/EdoFro/Freeplane_MarkdownHelper
package io.github.macmarrum.freeplane


import groovy.xml.XmlUtil
import org.freeplane.api.Node
import org.freeplane.core.util.HtmlUtils
import org.freeplane.core.util.TextUtils
import org.freeplane.plugin.script.FormulaUtils
import org.freeplane.plugin.script.FreeplaneScriptBaseClass
import org.freeplane.plugin.script.proxy.ScriptUtils

import java.text.MessageFormat
import java.util.regex.Pattern

class ConfluenceStorage {

    enum Flavor {
        CS, MD
    }

    public static Flavor flavor = Flavor.CS

    private static c = ScriptUtils.c()

    public static final String HILITE1ST = 'hiLite1st'
    private static final String KEY_TITLE = 'key:title'
    private static final String COLON = ':'
    private static final String NL = '\n'
    private static final String TRIPLE_SINGLE_QT = '\'\'\''
    private static final String BLANK = ''
    private static final String SPACE = ' '
    private static final String COMMA_SPACE = ', '

    public static style = [
            cStorageMarkupRoot : 'cStorageMarkupRoot',
            cStorageMarkupMaker: 'cStorageMarkupMaker',
    ]

    public static icon = [
            noEntry                     : 'emoji-26D4',
            eol_chequeredFlag           : 'emoji-1F3C1',
            noCsvSep_cancer             : 'emoji-264B',
            pButton                     : 'emoji-1F17F',
            nl_rightArrowCurvingDown    : 'emoji-2935',
            ol_keycapHash               : 'emoji-0023-20E3',
            ol_inputNumbers             : 'emoji-1F522',
            border_unchecked            : 'unchecked',
            nbsp_gemini                 : 'emoji-264A',
            numbers_inputNumbers        : 'emoji-1F522',
            collapse_fastUpButton       : 'emoji-23EB',
            xmlEscape_broom             : 'emoji-1F9F9',
            replacements_doubleCurlyLoop: 'emoji-27BF',
            stopAtThis_stopSign         : 'emoji-1F6D1',
            noSpaceAfter_lastQuarterMoon: 'emoji-1F317',
            forceMarkdown_circledM      : 'emoji-24C2',
    ]

    private static tbl = [
            rowCnt: '₵',
            rowNum: '№',
    ]

    static Boolean isMarkupRoot(Node n) {
        return n.style.name == style.cStorageMarkupRoot
    }

    static Boolean isMarkupMaker(Node n) {
        return n.style.name == style.cStorageMarkupMaker
    }

    static Boolean hasIcon(Node n, String icon) {
        return n.icons.icons.contains(icon)
    }

    static Boolean hasIcon(Node n, List<String> icons) {
        return n.icons.icons.any { it in icons }
    }

    /**
     * makeMarkup if it's a markup maker, otherwise
     * take note text if exists, else transformed text and
     * escape xml (if broom present),
     * only then do pReplacements (if doubleCurlyLoop present)
     * and replace each space with nbsp (if gemini present)
     */
    static String getContent(Node n) {
        String content = isMarkupMaker(n) ? makeMarkup(n) : n.note?.text ?: getTransformedPlainText(n)
        if (hasIcon(n, icon.xmlEscape_broom)) content = XmlUtil.escapeXml(content)
        if (hasIcon(n, icon.replacements_doubleCurlyLoop)) content = _applyReplacements(n, content)
        return hasIcon(n, icon.nbsp_gemini) ? content.replaceAll(/ /, '&nbsp;') : content
    }

    /**
     * Removes (possible) HTML from node.transformedText but keeps newlines (important for <pre>)
     */
    static String getTransformedPlainText(Node n) {
        def transformedText = n.transformedText
        if (transformedText && HtmlUtils.isHtml(transformedText))
            return HtmlUtils.htmlToPlain(transformedText, false, true)
        else
            return transformedText
    }

    /**
     * Like node.note.text but doesn't remove newlines (good for <pre>, bad for other tags)
     */
    static String getTransformedPlainNote(Node n) {
        def noteHtml = n.noteText
        if (noteHtml === null)
            return null
        else {
            def notePlain = HtmlUtils.htmlToPlain(noteHtml, false, false)
            return FormulaUtils.safeEvalIfScript(n.delegate, notePlain)
        }
    }

    /**
     * Like node.plainNote (unexposed) but doesn't remove newlines (good for <pre>, bad for other tags)
     */
    static String getPlainNote(Node n) {
        def noteHtml = n.noteText
        if (noteHtml === null)
            return null
        else
            return HtmlUtils.htmlToPlain(noteHtml, false, false)
    }

    private static mk = [
            parent      : 'parent',
            table       : 'table',
            list        : 'list',
            zip_list    : 'zip-list',
            quote       : 'quote',
            link        : 'link',
            expand      : 'expand',
            div         : 'div',
            code        : 'code',
            page_info   : 'page-info',
            div_expand  : 'div-expand',
            attachments : 'attachments',
            style_import: 'style-import',
            style       : 'style',
            html        : 'html',
            image       : 'image',
            csv         : 'csv',
            wiki        : 'wiki',
            markdown    : 'markdown',
            section     : 'section',
            column      : 'column',
            template    : 'template',
            format      : 'format',
    ]

    static String makeMarkup(Node n) {
        return switch (n.text) {
            case mk.parent -> mkParent(n)
            case mk.csv -> mkCsv(n)
            case mk.format -> mkFormat(n)
            case mk.template -> mkTemplate(n)
            case mk.table -> mkTable(n)
            case mk.list -> switch (flavor) {
                case Flavor.CS -> mkList(n)
                case Flavor.MD -> mkListMd(n)
            }
            case mk.zip_list -> mkZipList(n)
            case mk.quote -> mkQuote(n)
            case mk.link -> mkLink(n)
            case mk.expand -> switch (flavor) {
                case Flavor.CS -> mkExpand(n)
                case Flavor.MD -> mkExpandMd(n)
            }
            case mk.div -> switch (flavor) {
                case Flavor.CS -> mkDiv(n)
                case Flavor.MD -> mkDivMd(n)
            }
            case mk.code -> switch (flavor) {
                case Flavor.CS -> mkCode(n)
                case Flavor.MD -> mkCodeMd(n)
            }
            case mk.page_info -> switch (flavor) {
                case Flavor.CS -> mkPageInfo(n)
                case Flavor.MD -> mkPageInfoMd(n)
            }
            case mk.div_expand -> switch (flavor) {
                case Flavor.CS -> mkDivExpand(n)
                case Flavor.MD -> mkDivExpandMd(n)
            }
            case mk.attachments -> switch (flavor) {
                case Flavor.CS -> mkAttachments(n)
                case Flavor.MD -> mkAttachmentsMd(n)
            }
            case mk.style_import -> switch (flavor) {
                case Flavor.CS -> mkStyleImport(n)
                case Flavor.MD -> mkStyleImportMd(n)
            }
            case mk.style -> switch (flavor) {
                case Flavor.CS -> mkStyle(n)
                case Flavor.MD -> mkStyleMd(n)
            }
            case mk.html -> switch (flavor) {
                case Flavor.CS -> mkHtml(n)
                case Flavor.MD -> mkHtmlMd(n)
            }
            case mk.image -> switch (flavor) {
                case Flavor.CS -> mkImage(n)
                case Flavor.MD -> mkImageMd(n)
            }
            case mk.wiki -> switch (flavor) {
                case Flavor.CS -> mkWiki(n)
                case Flavor.MD -> mkWikiMd(n)
            }
            case mk.markdown -> switch (flavor) {
                case Flavor.CS -> mkMarkdown(n)
                case Flavor.MD -> mkMarkdownMd(n)
            }
            case mk.section -> switch (flavor) {
                case Flavor.CS -> mkSection(n)
                case Flavor.MD -> mkSectionMd(n)
            }
            case mk.column -> switch (flavor) {
                case Flavor.CS -> mkColumn(n)
                case Flavor.MD -> mkColumnMd(n)
            }
            default -> "<!-- mk function by that name not found: ${n.text} -->"
        }
    }

    static String getSpaceAfter(Node n) {
        return hasIcon(n, icon.noSpaceAfter_lastQuarterMoon) ? BLANK : SPACE
    }

    static String getNewLine(Node n) {
        return hasIcon(n, icon.nl_rightArrowCurvingDown) ? NL : BLANK
    }

    static String getEol(Node n) {
        return hasIcon(n, icon.eol_chequeredFlag) ? NL : BLANK
    }

    static String mkParent(Node n) {
        return _execIfChildren(n, { _mkParent(n) })
    }

    static String _execIfChildren(Node n, Closure closure) {
        if (n.children.size() > 0)
            return closure()
        else
            return '<!-- children are missing -->'
    }

    static String _mkParent(Node n) {
        def sb = new StringBuilder()
        def children = n.children
        def lastIdx = children.size() - 1
        children.eachWithIndex { it, i ->
            sb << mkNode(it)
            if (i < lastIdx)
                sb << getNewLine(n)
        }
        sb << getEol(n)
        return sb.toString()
    }

    private static Map<Pattern, String> pReplacements = new LinkedHashMap<Pattern, String>()
    static {
        pReplacements.put(~/(?m)(?<=^| )>> /, '➔ ')
        pReplacements.put(~/(?m)^== /, '➔ ')
        pReplacements.put(~/(?m)(?<=^| )>-> /, '↣ ')
        pReplacements.put(~/(?m)(?<=^| )-> /, '→ ')
        pReplacements.put(~/(?m)^=- /, '→ ')
        pReplacements.put(~/(?m)(?<=^| )=> /, '⇒ ')
        pReplacements.put(~/(?m)(?<=^|[^-])---(?=[^-]|$)/, '—')
        pReplacements.put(~/(?m)(?<=^|[^-])--(?=[^-]|$)/, '–')
        pReplacements.put(~/\{\{([^{]+)}}/, '<code>$1</code>')
        pReplacements.put(~/`([^`]+)`/, '<code>$1</code>')
        pReplacements.put(~/(?<=^|[^a-zA-Z0-9])\*(?! )(.+?)(?<! )\*(?=[^a-zA-Z0-9]|$)/, '<b>$1</b>')
        pReplacements.put(~/(?<=^|[^a-zA-Z0-9])_(?! )(.+?)(?<! )_(?=[^a-zA-Z0-9]|$)/, '<i>$1</i>')
        pReplacements.put(~/(?<=^|[^a-zA-Z0-9])-(?! )(.+?)(?<! )-(?=[^a-zA-Z0-9]|$)/, '<del>$1</del>')
        pReplacements.put(~/(?<=^|[^a-zA-Z0-9])\+(?! )(.+?)(?<! )\+(?=[^a-zA-Z0-9]|$)/, '<ins>$1</ins>')
        pReplacements.put(~/(?<=^|[^a-zA-Z0-9])\(([a-z]+)\)(?! )(.+?)(?<! )\(\/\1\)(?=[^a-zA-Z0-9]|$)/, '<span style="color: $1">$2</span>')
    }

    private static final RX_PLUS_ = ~/(?m)^\+(?= )/

    static String _applyReplacements(Node n, String content) {
        content = content.replaceAll(RX_PLUS_, getSimBullet(n))
        pReplacements.each {
            try {
                content = content.replaceAll(it.key, it.value)
            } catch (IndexOutOfBoundsException e) {
                println("** replaceAll(/${it.key.pattern()}/, /${it.value}/) for '$content'")
                throw e
            }
        }
        return content.replaceAll(/\n/, "<br />${getNewLine(n)}")
    }

    /**
     * Converts a branch to text (node and its children, recursively).
     * Adds a space after regular nodes, unless noSpaceAfter_lastQuarterMoon.
     * (!) No space after Markup Makers - use eol_chequeredFlag on each (as needed), or nl_rightArrowCurvingDown on mkParent (if used).
     * Adds a eol (nl) after each elem (except a markup maker, to avoid double eol) if eol_chequeredFlag.
     */
    static StringBuilder mkNode(Node n) {
        def result = new StringBuilder()
        if (hasIcon(n, icon.noEntry)) {
            return result
        } else {
            def eol = getEol(n)
            if (isMarkupMaker(n)) {
                result << makeMarkup(n) // eol added by markup maker
                return result
            } else {
                def nl = getNewLine(n)
                if (_isHeading(n)) {
                    result << _mkHeading(n, nl, eol) // also includes children
                    return result
                } else {
                    def isP = hasIcon(n, icon.pButton)
                    def sep = getSpaceAfter(n)
                    if (isP) { // no auto-replacements - getContent will do replacements if icon is set
                        result << '<p>' << nl << getContent(n) << sep
                    } else { // not a heading, not a P, must be a regular node
                        result << getContent(n) << sep << eol
                    }
                    if (!hasIcon(n, icon.stopAtThis_stopSign))
                        n.children.each { result << mkNode(it) }
                    if (isP)
                        result << nl << '</p>' << eol
                    return result
                }
            }
        }
    }

    static Boolean _isHeading(Node n) {
        return (n.icons.size() > 0 && n.icons.icons.any { it.startsWith('full-') })
    }

    static StringBuilder _mkHeading(Node n, String nl, String eol) {
        def hIcon = n.icons.icons.find { it.startsWith('full-') }
        def hLevel = hIcon[5..-1]
        def childrenBody = n.children.size() > 0 ? n.children.collect { mkNode(it) }.join(BLANK) : BLANK
        def result = new StringBuilder()
        switch (flavor) {
            case Flavor.CS ->
                result << '<h' << hLevel << '>' << nl << getContent(n) << nl << '</h' << hLevel << '>' << eol << childrenBody
            case Flavor.MD ->
                result << NL << NL << '#' * (hLevel as Integer) << SPACE << getContent(n) << NL << childrenBody
        }
        return result
    }


    /**
     * mkCsv is like mkParent but using getContent instead of mkNode (no headings or paragraphs)
     * + collects only the first child of each node
     * + uses a comma_space or any string as the separator, unless noCsvSep_cancer
     * + details-located csvSeps can be triple-single-quoted to preserve trailing space(s)
     * Adds space after the last item, unless noSpaceAfter_lastQuarterMoon on mkCsv
     */
    static StringBuilder mkCsv(Node n) {
        // sep can be defined as the attribute csvSep
        // sep can be defined in details
        // for table cells (details start with №), the default sep is used
        def sb = new StringBuilder()
        def children = n.children
        if (children.size() > 0) {
            def nl = getNewLine(n)
            def csvSep = n['csvSep'].text
            if (csvSep === null) {
                def detailsText = n.details?.text
                if (detailsText === null || detailsText.startsWith(tbl.rowNum))
                    csvSep = COMMA_SPACE
                else if (detailsText.size() > 6 && detailsText.startsWith(TRIPLE_SINGLE_QT) && detailsText.endsWith(TRIPLE_SINGLE_QT))
                    csvSep = detailsText[3..<-3] // remove surrounding quotes
                else
                    csvSep = detailsText
            }
            def cells = new LinkedList<Node>()
            children.each { Node it -> if (!hasIcon(it, icon.noEntry)) cells.addAll(getFirstChildChain(it)) }
            def lastIdx = cells.size() - 1
            cells.eachWithIndex { Node it, int i ->
                sb << getContent(it)
                if (i < lastIdx) { // not the last element
                    if (!hasIcon(it, icon.noCsvSep_cancer)) // noCsvSep_cancer is missing
                        sb << csvSep << nl
                } else  // last item – space if noSpaceAfter_lastQuarterMoon is missing
                    sb << getSpaceAfter(it) << getEol(it)
            }
            sb << getEol(n)
            // NB. getContent->makeMarkup->mk***->mkNode adds a trailing space (unless noSepAfter)
            return sb
        } else
            return sb << '<!-- a child is missing -->'
    }

    enum TemplateType {
        MESSAGE, STRING;
    }

    static String mkTemplate(Node n) {
        return _mkTemplate(n, TemplateType.MESSAGE)
    }

    static String mkFormat(Node n) {
        return _mkTemplate(n, TemplateType.STRING)
    }

    /**
     * Uses Pattern from the first child (note or transformed text).
     * Applies patternNode children (first-child chain only) to the Pattern.
     * Adds a space after unless noSpaceAfter_lastQuarterMoon.
     */
    static String _mkTemplate(Node n, TemplateType templateType) {
        def patternNode = n.children.find { !hasIcon(it, icon.noEntry) }
        if (!patternNode)
            return '<!-- a child (pattern) is missing -->'
        else {
            def yesEntryPatternChildren = patternNode.children.findAll { !hasIcon(it, icon.noEntry) }
            if (yesEntryPatternChildren.size() == 0)
                return '<!-- a (yes-entry) child is missing -->'
            else {
                def firstChildChain = yesEntryPatternChildren.collect { getFirstChildChain(it) }.flatten()
                def contentList = firstChildChain.collect { getContent(it) }
                def pattern = getContent(patternNode) + getSpaceAfter(patternNode)
                def result = switch (templateType) {
                    case TemplateType.MESSAGE -> MessageFormat.format(pattern, *contentList)
                    case TemplateType.STRING -> String.format(pattern, *contentList)
                    default -> throw new RuntimeException("unknown TemplateType: ${templateType.name()}")
                }
                return result + getEol(n)
            }
        }
    }

    enum HiLite1st {
        ROW, COLUMN, NONE
    }

    static StringBuilder mkTable(Node n) {
        def tableAnnotateText = getTableAnnotateText(n)
        def isToAnnotate = tableAnnotateText == annotate
        def isToClearAnnotations = tableAnnotateText == clear
        def nl = getNewLine(n)
        HiLite1st hiLite1st
        if (n[HILITE1ST]) {
            hiLite1st = HiLite1st.valueOf(n[HILITE1ST].text.toUpperCase())
        } else {
            hiLite1st = HiLite1st.NONE
        }
        def tableWiki = new StringBuilder()
        def colNum = 1
        def rowNum = 1
        tableWiki << '<table>' << nl
        switch (flavor) {
            case Flavor.CS -> tableWiki << '<colgroup><col /><col /></colgroup>' << nl << '<tbody>' << nl
        }
        // clean up details containing old tbl.rowCnt or tbl.rowNum
        if (isToAnnotate || isToClearAnnotations)
            n.findAll().drop(1).each { Node it -> if (it.detailsText && (it.details.text.startsWith(tbl.rowCnt) || it.details.text.startsWith(tbl.rowNum))) it.details = null }
        // the first column in each row is technical, therefore it's skipped
        n.children.findAll { !hasIcon(it, icon.noEntry) }.each { Node row ->
            def rowChildrenYesEntry = row.children.findAll { !hasIcon(it, icon.noEntry) }
            if (rowChildrenYesEntry.size() > 1) {  // each child (with descendants) is a column (vertical layout)
                tableWiki << '<tr>' << nl
                makeTableCellOfEachChildWithDescendantsAndAppendTo(tableWiki, rowChildrenYesEntry, rowNum, colNum, hiLite1st, nl, isToAnnotate)
                tableWiki << '</tr>' << nl // close the row
            } else {  // each first-child is a column (horizontal layout)
                final firstChildChainSize = _tbl_countFirstChildChain(row)
                if (isToAnnotate)
                    row.details = (new StringBuilder() << tbl.rowCnt << firstChildChainSize).toString()
                if (firstChildChainSize > 0) {
                    tableWiki << '<tr>' << nl
                    mkTableCellOfEachFirstChildInChainAndAppendTo(tableWiki, rowChildrenYesEntry[0], rowNum, colNum, hiLite1st, nl, isToAnnotate)
                    tableWiki << '</tr>' << nl // close the row
                }
                rowNum++
            }
        }
        switch (flavor) {
            case Flavor.CS -> tableWiki << '</tbody>' << nl
        }
        tableWiki << '</table>' << getSpaceAfter(n) << getEol(n)
        return tableWiki
    }

    static void makeTableCellOfEachChildWithDescendantsAndAppendTo(StringBuilder tableWiki, List<Node> children, int rowNum, int colNum, HiLite1st hiLite1st, String nl, boolean isToAnnotate) {
        children.each { Node child ->
            tableWiki << makeTableCell(this::mkNode, child, rowNum, colNum, hiLite1st, nl, isToAnnotate)
            colNum++
        }
    }

    static void mkTableCellOfEachFirstChildInChainAndAppendTo(StringBuilder tableWiki, Node n, int rowNum, int colNum, HiLite1st hiLite1st, String nl, boolean isToAnnotate) {
        tableWiki << makeTableCell(this::getContent, n, rowNum, colNum, hiLite1st, nl, isToAnnotate)
        // shouldOmitMarkupMaker=false because each cell is basically a top-level node, i.e. can be a cStorageMarkupMaker
        Node firstChildIfNotIgnoreNode = getFirstChildIfNotIgnoreNode(n, false)
        if (firstChildIfNotIgnoreNode) {
            colNum++
            mkTableCellOfEachFirstChildInChainAndAppendTo(tableWiki, firstChildIfNotIgnoreNode, rowNum, colNum, hiLite1st, nl, isToAnnotate)
        }
    }

    static StringBuilder makeTableCell(Closure method, Node n, int rowNum, int colNum, HiLite1st hiLite1st, String nl, boolean isToAnnotate) {
        def result = new StringBuilder()
        if (isToAnnotate)
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
    private static final String clear = 'clear'
    private static final String annotate = 'annotate'
    private static final String none = 'none'

    private static final ArrayList<String> clear_annotate_none = [clear, annotate, none]

    static String getTableAnnotateText(Node n) {
        def detailsText = n.details?.text
        if (detailsText in clear_annotate_none)
            return detailsText
        def markupRoot = n.pathToRoot.find { isMarkupRoot(it) }
        def markupRootDetailsText = markupRoot.details?.text
        if (markupRootDetailsText in clear_annotate_none)
            return markupRootDetailsText
    }

    /**
     * shouldOmitMarkupMaker for getFirstChildChain, used by mkCsv – to stop at markupMaker nodes
     */
    static Node getFirstChildIfNotIgnoreNode(Node n, boolean shouldOmitMarkupMaker = true) {
        def isMarkupMakerAndCanExcludeIt = isMarkupMaker(n) && shouldOmitMarkupMaker
        if (isMarkupMakerAndCanExcludeIt || n.children.size() == 0 || hasIcon(n, icon.stopAtThis_stopSign) || hasIcon(n.children[0], icon.noEntry))
            return null
        else
            return n.children[0]
    }

    static int _tbl_countFirstChildChain(Node n, int cnt = 0) {
        def child = getFirstChildIfNotIgnoreNode(n, false)
        if (child)
            return _tbl_countFirstChildChain(child, ++cnt)
        else
            return cnt
    }

    private static olIcons = [icon.ol_keycapHash, icon.ol_inputNumbers]

    static StringBuilder mkList(Node n) {
        def nl = getNewLine(n)
        def result = new StringBuilder()
        def tag = hasIcon(n, olIcons) ? '<ol>' : '<ul>'
        result << tag << nl
        String body
        n.children.each {
            body = mkNode(it)
            if (body.trim().size() > 0)
                result << '<li>' << nl << body << nl << '</li>' << nl
        }
        result << '</' << tag[1..-1] << getSpaceAfter(n) << getEol(n)
        return result
    }

    static StringBuilder mkListMd(Node n) {
        if (hasIcon(n, icon.forceMarkdown_circledM)) {
            def result = new StringBuilder()
            def item_prefix = hasIcon(n, olIcons) ? '1. ' : '* '
            String body
            n.children.each {
                body = getFirstChildChain(it)*.text.join(SPACE)
                if (body.trim().size() > 0)
                    result << item_prefix << body << NL
            }
            result << getSpaceAfter(n) << getEol(n)
            return result
        } else {
            return mkList(n)
        }
    }

    /**
     * mkZipList, like mkCsv, uses its own content gatherer, instead of mkNode, to consider only the first child on each level
     *  TODO: consider if mkNode could be used instead, especially now that <div> is used in place of <p>
     *      -- benefits / disadvantages
     */
    static StringBuilder mkZipList(Node n) {
        def nl = getNewLine(n)
        def bullet = getSimBullet(n)
        int smallerBranchChildrenSize = n.children.collect { branch -> branch.children.size() }.min()
        def items = new LinkedHashMap<Integer, StringBuilder>()
        n.children.each { branch ->
            branch.children.eachWithIndex { levelOneChild, int idx ->
                if (idx < smallerBranchChildrenSize) {
                    if (!items[idx]) {
                        items[idx] = new StringBuilder() << bullet << SPACE
                    }
                    items[idx] << getEachFirstChildsContent(levelOneChild, SPACE, true)  // skip levelOneChild, which is a numbering
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
        sb << nl << '</div>' << getSpaceAfter(n) << getEol(n)
        return sb
    }

    static String getSimBullet(Node n) {
        return n['simBullet'].text ?: '●'
    }

    /**
     * shouldOmitMarkupMaker for getFirstChildIfNotIgnoreNode
     * then for getFirstChildChain, used by mkCsv – to stop at markupMaker nodes
     */
    static StringBuilder getEachFirstChildsContent(n, String sep = SPACE, boolean shouldOmitMarkupMaker = true) {
        /* shouldOmitMarkupMaker for top-level nodes of mkSomething */
        def child = getFirstChildIfNotIgnoreNode(n, shouldOmitMarkupMaker)
        final sb = new StringBuilder()
        if (child) {
            sb << getContent(child)
            if (!hasIcon(child, icon.noSpaceAfter_lastQuarterMoon))
                sb << sep
            final grandchildsContent = getEachFirstChildsContent(child, sep)
            if (!grandchildsContent.isBlank())
                sb << grandchildsContent
        }
        return sb
    }

    static List<Node> getFirstChildChain(Node n, List<Node> firstChildChain = null) {
        if (firstChildChain.is(null))
            firstChildChain = new LinkedList<Node>()
        firstChildChain.add(n)
        def child = getFirstChildIfNotIgnoreNode(n)
        if (child)
            return getFirstChildChain(child, firstChildChain)
        else
            return firstChildChain
    }

    static String mkQuote(Node n) {
        def nl = getNewLine(n)
        def result = new StringBuilder()
        result << '<blockquote>' << nl << mkParent(n) << '</blockquote>' << getSpaceAfter(n) << getEol(n)
        return result.toString()
    }

    static String mkLink(Node n) {
        /* make link of the first child with a link */
        def nl = getNewLine(n)
        for (child in n.children.find { it.link }) {
            def result = new StringBuilder()
            result << '<a href="' << XmlUtil.escapeXml(child.link.text) << '">' << nl << getContent(child) << nl << '</a>' << getSpaceAfter(n) << getEol(n)
            return result.toString()
        }
        return '<!-- a child with a link is missing -->'
    }

    static String getUuid(Node n) {
        // e.g. '649ac91e-33a1-476c-93d2-a30170e197a3'
        return UUID.randomUUID().toString()
    }

    static String mkExpand(Node n) {
        return _execIfChildren(n, {
            _mkMacroRich(n, 'expand', [title: n.details?.text ?: 'Click here to expand...'])
        })
    }

    static String mkExpandMd(Node n) {
        def nl = getNewLine(n)
        def sb = new StringBuilder()
        sb << NL
        sb << '<details>' << nl
        sb << '<summary>' << (n.details?.text ?: 'Click here to expand...') << '</summary>' << NL
        sb << _mkParent(n) // mkParent (mkNode) adds nl, if needed
        sb << '</details>' << getSpaceAfter(n) << getEol(n)
        return sb.toString()
    }

    static String mkDiv(Node n) {
        return _execIfChildren(n, {
            def detailsText = n.details?.text
            Map<String, String> params = detailsText ? [class: detailsText] : null
            return _mkMacroRich(n, 'div', params)
        })
    }

    static String mkDivMd(Node n) {
        def nl = getNewLine(n)
        def sb = new StringBuilder()
        def detailsText = n.details?.text
        sb << '<div'
        if (detailsText) {
            sb << ' class="' << detailsText << '"'
        }
        sb << '>' << nl
        sb << _mkParent(n) // mkParent (mkNode) adds nl, if needed
        sb << nl << '</div>' << getSpaceAfter(n) << getEol(n)
        return sb.toString()
    }

    static String mkCode(Node n) {
        // https://confluence.atlassian.com/doc/code-block-macro-139390.html
        // final LANGUAGES = ['ActionScript', 'AppleScript', 'Bash', 'C#', 'C++', 'CSS', 'ColdFusion', 'Delphi', 'Diff', 'Erlang', 'Groovy', 'HTML and XML', 'Java', 'Java FX', 'JavaScript', 'PHP', 'Plain Text', 'PowerShell', 'Python', 'Ruby', 'SQL', 'Sass', 'Scala', 'Visual Basic', 'YAML']
        final MAX_LANG_SIZE = 12
        for (child in n.children.find { Node it -> it.children.size() > 0 || it.noteText !== null || it.detailsText !== null }) {
            String lang
            String cdata  // get cdata from childText, alternatively from children or from note
            String childText = child.text
            String title
            String childDetailsText = child.details?.text
            String nDetailsText
            String childLinkText
            if (childText.size() > MAX_LANG_SIZE) { // childText is not a language name
                lang = childDetailsText ?: 'none'
                cdata = childText
                if (nDetailsText = n.details?.text)
                    title = nDetailsText
            } else {
                lang = childText ?: 'none'
                if (child.children.size() > 0)
                    cdata = mkParent(child)
                else
                    cdata = child.plainNote  // no formula evaluation (unexposed method)
                if (childDetailsText)
                    title = childDetailsText
            }
            def params = [language: lang]
            if (title)
                params.title = title
            if (hasIcon(child, icon.collapse_fastUpButton))
                params.collapse = 'true'
            if (hasIcon(child, icon.numbers_inputNumbers))
                params.linenumbers = 'true'
            if (childLinkText = child.link.text)  // link is never null
                params.theme = childLinkText
            return _mkMacroPlain(n, 'code', cdata, params)
        }
        return '<!-- a child with children or a note is missing -->'
    }

    static String mkCodeMd(Node n) {
        final MAX_LANG_SIZE = 12
        for (child in n.children.find { Node it -> it.children.size() > 0 || it.noteText !== null || it.detailsText !== null }) {
            String lang
            String cdata  // get cdata from childText, alternatively from children or from note
            String childText = child.text
            String title = 'Code'
            String childDetailsText = child.details?.text
            String nDetailsText
            String childLinkText
            if (childText.size() > MAX_LANG_SIZE) { // childText is not a language name
                lang = childDetailsText ?: 'none'
                cdata = childText
                if (nDetailsText = n.details?.text)
                    title = nDetailsText
            } else {
                lang = childText ?: 'none'
                if (child.children.size() > 0)
                    cdata = mkParent(child)
                else
                    cdata = child.plainNote  // no formula evaluation (unexposed method)
                if (childDetailsText)
                    title = childDetailsText
            }
            def sb = new StringBuilder()
            def isToBeCollapsed = hasIcon(child, icon.collapse_fastUpButton)
            if (isToBeCollapsed) {
                sb << NL << '<details>' << '<summary>' << title << '</summary>' << NL
            }
            sb << NL
            sb << '```' << lang << NL
            sb << cdata << NL // mkParent (mkNode) adds nl, if needed
            sb << '```' << NL
            if (isToBeCollapsed) {
                sb << '</details>' << getSpaceAfter(n) << getEol(n)
            }
            return sb.toString()
        }
        return '<!-- a child with children or a note is missing -->'
    }

    static String mkPageInfo(Node n) {
        String infoType = n.details?.text ?: 'Title'
        return _mkPageInfo(n, infoType)
    }

    static String mkPageInfoMd(Node n) {
        String infoType = n.details?.text ?: 'Title'
        return "<!-- ${mk.page_info} ${infoType} -->&#10008;${getSpaceAfter(n)}${getEol(n)}".toString()
    }

    static String _mkPageInfo(Node n, String infoType, String type = 'Flat') {
        /* Page id, Current version, Tiny url, Title, ...  */
        return _mkMacroPlain(n, 'page-info', null, [infoType: infoType, type: type])
    }

    static String _mkMacroPlain(Node n, String macro, String cdata, Map<String, String> parameters = null) {
        def nl = getNewLine(n)
        def result = new StringBuilder()
        result << '<ac:structured-macro ac:name="' << macro << '" ac:schema-version="1" ac:macro-id="' << getUuid(n) << '">' << nl
        if (parameters)
            parameters.each {
                result << '<ac:parameter ac:name="' << it.key << '">' << it.value << '</ac:parameter>' << nl
            }
        if (cdata)
            result << '<ac:plain-text-body>' << nl << '<![CDATA[' << cdata << ']]>' << nl << '</ac:plain-text-body>' << nl
        result << '</ac:structured-macro>' << getSpaceAfter(n) << getEol(n)
        return result.toString()
    }

    static StringBuilder _mkMacroRich(Node n, String macro, Map<String, String> parameters = null, String body = null) {
        def nl = getNewLine(n)
        def result = new StringBuilder()
        def body_nl = body ? body + nl : _mkParent(n) // mkParent (mkNode) adds nl, if needed
        result << '<ac:structured-macro ac:name="' << macro << '" ac:schema-version="1" ac:macro-id="' << getUuid(n) << '">' << nl
        if (parameters)
            parameters.each {
                result << '<ac:parameter ac:name="' << it.key << '">' << it.value << '</ac:parameter>' << nl
            }
        result << '<ac:rich-text-body>' << nl << body_nl << '</ac:rich-text-body>' << nl
        result << '</ac:structured-macro>' << getSpaceAfter(n) << getEol(n)
        return result
    }

    static String mkDivExpand(Node n) {
        return _execIfChildren(n, {
            def nl = getNewLine(n)
            def title = n.details?.text ?: 'Click here to expand...'
            def className = n.link.text ?: 'expand-in-a-box'
            def result = new StringBuilder()
            result << '<div class="' << className << '">' << nl
            result << _mkMacroRich(n, 'expand', [title: title]) << nl
            result << '</div>'
            return result.toString()
        })
    }

    static String mkDivExpandMd(Node n) {
        return mkExpandMd(n)
    }

    static String mkAttachments(Node n) {
        def nl = getNewLine(n)
        def canUpload = n['attachmentsUpload'].num0 == 1 ? 'true' : 'false'
        def canOld = n['attachmentsOld'].num0 == 1 ? 'true' : 'false'
        def result = new StringBuilder()
        result << '<ac:structured-macro ac:name="attachments" ac:schema-version="1" ac:macro-id="' << getUuid(n) << '">' << nl
        result << '<ac:parameter ac:name="upload">' << canUpload << '</ac:parameter>' << nl
        result << '<ac:parameter ac:name="old">' << canOld << '</ac:parameter>' << nl << '</ac:structured-macro>' << getEol(n)
        return result.toString()
    }

    static String mkAttachmentsMd(Node n) {
        return "<i>Visible only on Confluence</i>${getSpaceAfter(n)}${getEol(n)}".toString()
    }

    static String mkStyleImport(Node n) {
        for (child in n.children.find { Node it -> it.text }) {
            return _mkMacroPlain(n, 'style', null, [import: child.text])
        }
        return '<!-- a child with text is missing -->'
    }

    static String mkStyleImportMd(Node n) {
        return "<!-- ${mk.style_import} -->${getSpaceAfter(n)}${getEol(n)}".toString()
    }

    static String mkStyle(Node n) {
        return _execIfChildren(n, {
            return _mkMacroPlain(n, 'style', _mkParent(n))
        })
    }

    static String mkStyleMd(Node n) {
        return "<!-- ${mk.style} -->${getSpaceAfter(n)}${getEol(n)}".toString()
    }

    static String mkHtml(Node n) {
        return _execIfChildren(n, {
            return _mkMacroPlain(n, 'html', _mkParent(n))
        })
    }

    static String mkHtmlMd(Node n) {
        return mkParent(n)
    }

    /**
     * To set a height, put a number in details.
     * To add a border, use icon `unchecked`.
     * To link an image attached in a different page, put `page` attribute with <space key>:<page title>
     */
    static String mkImage(Node n) {
        String detailsText
        for (child in n.children.find { Node it -> it.text }) {
            def result = new StringBuilder()
            result << '<ac:image '
            if (detailsText = child.details?.text)
                result << 'ac:height="' << detailsText << '" '
            if (hasIcon(child, icon.border_unchecked))
                result << 'ac:border="true" '
            result << '>'
            result << '<ri:attachment ri:filename="' << child.text
            def key_title = child[KEY_TITLE].text
            if (key_title && key_title.contains(COLON)) {
                result << '">'
                def (spaceKey, contentTitle) = key_title.split(COLON, 2)
                result << '<ri:page ri:space-key="' << spaceKey << '" ri:content-title="' << contentTitle << '" /></ri:attachment>'
            } else {
                result << '" />'
                println("** ConfluenceStorage - mkImage - no `${COLON}` in node['${KEY_TITLE}'].text")
            }
            result << '</ac:image>' << getEol(n)
            return result.toString()
        }
        return '<!-- a child with text is missing -->'
    }

    static String mkImageMd(Node n) {
        return _wrapIntoCode(n, mkImage(n), 'XML')
    }

    static String _wrapIntoCode(Node n, String body, String lang = 'none') {
        def sb = new StringBuilder()
        sb << NL
        sb << '```' << lang << NL
        sb << body.replaceFirst(/\n+$/, '') << NL
        sb << '```' << getEol(n)
    }

    static String mkWiki(Node n) {
        return _execIfChildren(n, {
            return _mkMacroPlain(n, 'unmigrated-wiki-markup', _mkParent(n))
        })
    }

    static String mkWikiMd(Node n) {
        return _wrapIntoCode(n, mkParent(n), 'Markdown')
    }

    static String mkMarkdown(Node n) {
        return _execIfChildren(n, {
            return _mkMacroPlain(n, 'markdown', _mkParent(n))
        })
    }

    static String mkMarkdownMd(Node n) {
        return mkParent(n)
    }

    static String mkSection(Node n) {
        return _execIfChildren(n, {
            Map<String, String> params = n.icons.contains(icon.border_unchecked) ? [border: 'true'] : null
            return _mkMacroRich(n, 'section', params)
        })
    }

    static String mkSectionMd(Node n) {
        return _wrapIntoCode(n, mkSection(n), 'XML')
    }

    static String mkColumn(Node n) {
        return _execIfChildren(n, {
            def detailsText = n.details?.text
            Map<String, String> params = detailsText ? [width: detailsText] : null
            return _mkMacroRich(n, 'column', params)
        })
    }

    static String mkColumnMd(Node n) {
        return _wrapIntoCode(n, mkColumn(n), 'XML')
    }

    static Node createMarkupMaker(Node node, String name) {
        def maker = node.createChild(name)
        maker.style.name = style.cStorageMarkupMaker
        c.select(maker)
        return maker
    }

    static List<Node> createCode(Node node, String language = 'sql', String title = null, boolean showLineNumbers = true, String theme = 'Midnight', boolean collapse = false, String styleName = '=Code') {
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
        try {
            code.style.name = styleName
        } catch (IllegalArgumentException ignored) {
        }
        c.select(code)
        return [n, code]
    }

    static List<Node> createDivExpandCode(Node node, String details = 'Expand: SQL Statement', String cssClassName = 'macmarrum-expand', String language = 'sql', boolean showLineNumbers = true, String theme = 'Midnight') {
        def n = createMarkupMaker(node, 'div-expand')
        n.details = details
        n.link.text = cssClassName
        n.icons.add(icon.nl_rightArrowCurvingDown)
        def (codeNode, code) = createCode(n, language, null, showLineNumbers, theme)
        return [n, codeNode, code]
    }

    static Node createList(Node node) {
        def n = createMarkupMaker(node, 'list')
        n.icons.add(icon.nl_rightArrowCurvingDown)
        return n
    }

    static Node createLink(Node node) {
        return createMarkupMaker(node, 'link')
    }

    static List<Node> createFormat(Node node, String pattern = '*%s*') {
        def n = createMarkupMaker(node, 'format')
        def p = n.createChild(pattern)
        p.icons.add(icon.replacements_doubleCurlyLoop)
        p.icons.add(icon.noSpaceAfter_lastQuarterMoon)
        c.select(p)
        return [n, p]
    }

    static List<Node> createTable(Node node, String styleName = '=Numbering#') {
        def n = createMarkupMaker(node, 'table')
        n.icons.add(icon.nl_rightArrowCurvingDown)
        n[HILITE1ST] = HiLite1st.COLUMN
        def elem = n.createChild()
        elem.style.name = styleName
        c.select(elem)
        return [n, elem]
    }

    static Node createParent(Node node) {
        return createMarkupMaker(node, 'parent')
    }

    static Node createCsv(Node node) {
        return createMarkupMaker(node, 'csv')
    }

    static List<Node> createImage(Node node) {
        def n = createMarkupMaker(node, 'image')
        def o = n.createChild('filename.png')
        o[KEY_TITLE] = BLANK
        c.select(o)
        return [n, o]
    }

    static void copyMarkup(Node n) {
        Node target
        if (style.containsKey(n.style.name)) // ['cStorageMarkupRoot', 'cStorageMarkupMaker']
            target = n
        else
            target = n.pathToRoot.reverse().find { it.style.name == style.cStorageMarkupRoot }

        if (target) {
            String markup = makeMarkup(target)
            TextUtils.copyToClipboard(markup)
            c.statusInfo = 'Confluence-storage markup copied to clipboard'
            openInEditorIfDefined(target, markup)
        } else {
            c.statusInfo = "cannot copy ConfluenceStorage Markup because the node style is not in ${style*.value}"
        }
    }

    /** Uses _command_after_copying_cstorage_markup to define the editor -- must be in PATH
     */
    static void openInEditorIfDefined(Node node, String markup) {
        def config = new FreeplaneScriptBaseClass.ConfigProperties()
        def _command_after_copying_cstorage_markup = config.getProperty('_command_after_copying_cstorage_markup')
        if (_command_after_copying_cstorage_markup && !_command_after_copying_cstorage_markup.startsWith('disable')) {
            File mmFile = node.mindMap.file
            def xmlFileBasename = mmFile.name.replaceFirst(/\.mm$/, '.cStorage')
            def xmlFile = new File(mmFile.parent, xmlFileBasename)
            try {
                xmlFile.withWriter('UTF-8') {
                    it << '<!-- vim: set ft=xml: -->\n'
                    it << markup
                }
                [_command_after_copying_cstorage_markup, xmlFile.path].execute()
            } catch (RuntimeException e) {
                c.statusInfo = e.message
            }
        }
    }
}
