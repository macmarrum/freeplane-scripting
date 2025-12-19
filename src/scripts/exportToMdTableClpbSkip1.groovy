// Copyright (C) 2023, 2024, 2025  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Export"})
import io.github.macmarrum.freeplane.Export
import org.freeplane.core.util.TextUtils

def settings = [skip1: true, sep: '|', frame: true, nl: '\u00B6', quote: 'NONE']
def text = Export.toCsvString(node, settings)
def line = text.split(/\n/, 2)[0]
def numOfSeps = line.count('|')
def mdTable = "${'|' * numOfSeps}\n${'|-' * (numOfSeps - 1)}|\n${text}"
TextUtils.copyToClipboard(mdTable)
