/*
 * Copyright (C) 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Gmail"})


import org.freeplane.api.Controller
import org.freeplane.core.ui.components.UITools

import java.awt.*

c = c as Controller
def details = c.selected.details?.text
def scriptTitle = 'Gmail Search By Subject'
if (!details) {
    UITools.informationMessage(UITools.currentFrame, 'No Details', scriptTitle, 0)
} else {
    def userNum = 0
    def searchUrlBase = "https://mail.google.com/mail/u/${userNum}/#search/"
    def searchTerm = "subject:\"${details}\""
    def encodedSearchTerm = URLEncoder.encode(searchTerm, 'UTF-8')
    def searchUrl = "${searchUrlBase}${encodedSearchTerm}"
    c.statusInfo = "${scriptTitle}: ${searchUrl}"
    if (Desktop.isDesktopSupported()) {
        def d = Desktop.getDesktop()
        if (d.isSupported(Desktop.Action.BROWSE)) {
            d.browse(new URI(searchUrl))
        }
    }
}
