/**
 * Copyright (C) 2024 - 2025  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Export"})
import io.github.macmarrum.freeplane.Export
import org.freeplane.api.Node
import org.freeplane.core.util.TextUtils

node = node as Node
def settings = [
//        core           : false, // default: true
//        details        : false, // default: true
//        note           : false, // default: true
//        attributes     : false, // default: true
        transformed    : false, // default: true
        plain          : false, // default: true
        format         : true, // default: false
        style          : true, // default: false
//        formatting     : true, // default: false
        icons          : true, // default: false
        tags           : true, // default: false
        link           : true, // default: false
        skip1          : true, // default: false
        denullify      : true, // default: false
        pretty         : true, // default: false
//        forceId        : true, // default: false
//        forceAttribList: true, // default: false
//        dateFmt        : Export.DateFmt.ISO_LOCAL,
]
def text = Export.toJsonString(node, settings)
TextUtils.copyToClipboard(text)

def viewJsonInGvim = false
if (viewJsonInGvim) {
    def tempFile = File.createTempFile(node.id + '~', '.json')
    tempFile.setText(text, 'UTF-8')
    def gvimProcess = ['gvim', '--nofork', tempFile].execute()
    gvimProcess.waitFor()
    tempFile.delete()
}
