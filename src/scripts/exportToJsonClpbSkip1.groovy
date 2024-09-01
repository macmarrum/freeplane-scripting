/**
 * Copyright (C) 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Export"})
import io.github.macmarrum.freeplane.Export
import org.freeplane.api.Node
import org.freeplane.core.util.TextUtils

def node = node as Node
def settings = [
//        details    : true,
//        note       : true,
//        attributes : true,
//        transformed: true,
//        dateFmt    : Export.DateFmt.ISO_LOCAL,
//        format     : false,
//        style      : true,
//        formatting : true,
//        icons      : true,
//        tags       : true,
//        link       : true,
        skip1      : true,
        denullify  : true,
        pretty     : true,
//        forceId    : false,
//        forceAttribList: false,
]
def text = Export.toJsonString(node, settings)
TextUtils.copyToClipboard(text)
