// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})

import org.freeplane.api.Node
import org.freeplane.core.util.HtmlUtils

c.selecteds.each { Node it ->
    def lnk = it.link.text
    if (lnk) {
        def prefix = ' '
        it['pasetAsSymlinkUri'] = prefix + lnk
        String text
        if (it.text) {
            it['pasteAsSymlinkText'] = prefix + it.text
            text = it.transformedText
            it.text = text
        }
        if (it.detailsText) {
            // save the oroiginal formula
            it['pasteAsSymlinkDetails'] = prefix + HtmlUtils.htmlToPlain(it.detailsText)
            text = it.details
            it.details = text
        }
        if (it.noteText) {
            it['pasteAsSymlinkNote'] = prefix + HtmlUtils.htmlToPlain(it.noteText)
            text = it.note
            it.note = text
        }
    }
}
