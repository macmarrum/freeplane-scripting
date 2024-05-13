// Copyright (C) 2023, 2024  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Export"})
import io.github.macmarrum.freeplane.Export
import org.freeplane.core.util.TextUtils

def settings = [sep: '|', tail: true, skip1: true]
def text = Export.toCsvString(node, settings)
TextUtils.copyToClipboard(text)
