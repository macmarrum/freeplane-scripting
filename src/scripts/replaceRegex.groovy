/*
 * Copyright (C) 2022, 2024, 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1"})


import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.resources.ResourceController
import org.freeplane.core.ui.components.UITools
import org.freeplane.features.map.NodeModel

c = c as Controller
final String _last_replace_regex_expression = "_last_replace_regex_expression"
final resourceController = ResourceController.getResourceController()
final NodeModel nodeModel = c.selected.delegate
final String initialValue = resourceController.getProperty(_last_replace_regex_expression)
// printf-style → https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax
String input = UITools.showInputDialog(nodeModel, '''Replace Regex
E.g. /pattern/replacement/g
Use printf-style, e.g. %n, for pattern and replacement
NB Special characters <([{\\^-=$!|]})?*+.>''', initialValue)
if (input !== null) {
    resourceController.setProperty(_last_replace_regex_expression, input)
    final token = input[0]
    final tokenCount = input.count(token)
    if (tokenCount != 3) {
        c.statusInfo = "Replace Regex: token count is $tokenCount - expected 3"
        return
    }
    final lst = input.tokenize(token)
    final String pattern = sprintf(lst[0])
    final boolean isGlobal = input.endsWith("${token}g")
    String replacement
    if ((isGlobal && input.endsWith("$token${token}g")) || input.endsWith("$token$token"))
        replacement = ''
    else
        replacement = sprintf(lst[1])
    c.selecteds.each { Node n ->
        if (isGlobal)
            n.text = n.text.replaceAll(pattern, replacement)
        else
            n.text = n.text.replaceFirst(pattern, replacement)
    }
}
