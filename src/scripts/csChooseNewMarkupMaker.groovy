// Copyright (C) 2023, 2024  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/CStorage"})


import io.github.macmarrum.freeplane.ConfluenceStorage
import org.freeplane.api.Controller

import javax.swing.*

c = c as Controller
final title = 'Insert markup maker'
final action = [
        'list': ConfluenceStorage::createList,
        'link': ConfluenceStorage::createLink,
        'table': ConfluenceStorage::createTable,
        'code': ConfluenceStorage::createCode,
        'csv': ConfluenceStorage::createCsv,
        'div-expand+code': ConfluenceStorage::createDivExpandCode,
        'format': ConfluenceStorage::createFormat,
        'parent': ConfluenceStorage::createParent,
        'image': ConfluenceStorage::createImage,
]
String input = JOptionPane.showInputDialog(null, null, title,
        JOptionPane.QUESTION_MESSAGE, null, action.keySet().toArray(), null)
if (input == null || input == '') {
    c.statusInfo = 'input is null or blank'
    return
} else {
    def method = action.getOrDefault(input, null)
    if (method)
        method(node)
    else
        c.statusInfo = "no method by key $input"
}
