// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})

import org.freeplane.api.Node
import org.freeplane.core.util.HtmlUtils

String lnk
String text
def prefix = ' '
c.selecteds.each { Node it ->
    lnk = it.link.text
    if (lnk) {
        it['pasteAsSymlinkUri'] = prefix + lnk
        if (it.text.startsWith('=')) {
            // save the original formula
            it['pasteAsSymlinkText'] = prefix + it.text
            // materialize content
            text = it.transformedText
            it.text = text
        }
        def detailsText = it.detailsText
        if (detailsText) {
            def plainDetailsText = HtmlUtils.htmlToPlain(detailsText)
            if (plainDetailsText.startsWith('=')) {
                // save the original formula
                it['pasteAsSymlinkDetails'] = prefix + plainDetailsText
                // materialize content
                text = it.details
                it.details = text
            }
        }
        def noteText = it.noteText
        if (noteText) {
            def plainNoteText = HtmlUtils.htmlToPlain(noteText)
            if (plainNoteText.startsWith('=')) {
                // save the original formula
                it['pasteAsSymlinkNote'] = prefix + plainNoteText
                // materialize content
                text = it.note
                it.note = text
            }
        }
    }
}
