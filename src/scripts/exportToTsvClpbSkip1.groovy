// Copyright (C) 2023, 2024  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Export"})
import io.github.macmarrum.freeplane.Export
import org.freeplane.api.Node
import org.freeplane.core.util.TextUtils

def settings = [sep: '\t', skip1: true]
String text = c.selecteds.collect { Node node -> Export.toCsvString(node, settings) }.join('')
TextUtils.copyToClipboard(text)
