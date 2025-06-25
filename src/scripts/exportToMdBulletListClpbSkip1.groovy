/*
 * Copyright (C) 2025  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Export"})


import io.github.macmarrum.freeplane.Export
import org.freeplane.api.Node
import org.freeplane.core.util.TextUtils

node = node as Node
def settings = [
//        mkBullet   : { int level, Node n -> '  ' * (level - 1) + '- ' },
//        transformed: true,
//        plain      : true,
//        nl         : '\u006B', // pilcrow
//        link       : true,
skip1: true,
]
def bulletList = Export.toBulletListString(node, settings)
TextUtils.copyToClipboard(bulletList)
