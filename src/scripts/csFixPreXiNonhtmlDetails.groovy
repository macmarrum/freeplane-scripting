/**
 * Copyright (C) 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * Markdown is stored in .mm by Freeplane 1.10.x and 1.11.x as CONTENT-TYPE="plain/markdown" with <text>.
 * Only in case of DETAILS, when quotable chars like ' (&apos;) are there, and when Details are first set to Markdown then to Standard/Text/LaTeX,
 * v1.10.x quotes the chars, whereas v1.11.x doesn't. So when .mm saved by v1.10.x is opened by v1.11.x,
 * the chars aren't unquoted, causing <text> tags themselves to be wrongly interpreted as literal content.
 * To correct it, in v1.10.x run the script to revert the Standard/Text/LaTeX Details from <text> to <html>
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/CStorage"})


import org.freeplane.api.Controller
import org.freeplane.core.ui.components.UITools
import org.freeplane.core.util.FreeplaneVersion

if (!FreeplaneVersion.version.isOlderThan(new FreeplaneVersion(1, 11, 0))) {
    UITools.showMessage('This script is designed to be run only in a Freeplane earlier than 1.11.x', 0)
} else {
    def c = c as Controller
    c.findAll().each {
        def d = it.detailsText
        if (d && it.detailsContentType != 'markdown' && !d.startsWith('<html>')) {
            it.details = null
            it.details = d
        }
    }
}
