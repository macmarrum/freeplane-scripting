// Copyright (C) 2022-2024  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/CStorage"})

import io.github.macmarrum.freeplane.ConfluenceStorage
import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.util.TextUtils
import org.freeplane.plugin.script.FreeplaneScriptBaseClass

node = node as Node
c = c as Controller

Node target
if (node.style.name in ConfluenceStorage.style) // ['cStorageMarkupRoot', 'cStorageMarkupMaker'])
    target = node
else
    target = node.pathToRoot.reverse().find { it.style.name == 'cStorageMarkupRoot' }

if (target) {
    String markup = ConfluenceStorage.makeMarkup(target)
    TextUtils.copyToClipboard(markup)
    c.statusInfo = 'Confluence-storage markdown copied to clipboard'
    openInEditorIfDefined(c, target, markup)
} else {
    c.statusInfo = "cannot copy ConfluenceStorage Markup because the node style is not in ${ConfluenceStorage.style*.value}"
}

/*
 * Uses _command_after_copying_cstorage_markup to define the editor -- must be in PATH
 */
static void openInEditorIfDefined(Controller c, Node node, String markup) {
    def config = new FreeplaneScriptBaseClass.ConfigProperties()
    def _command_after_copying_cstorage_markup = config.getProperty('_command_after_copying_cstorage_markup')
    if (_command_after_copying_cstorage_markup && !_command_after_copying_cstorage_markup.startsWith('disable')) {
        File mmFile = node.mindMap.file
        def xmlFileBasename = mmFile.name.replaceFirst(/\.mm$/, '.cStorage')
        def xmlFile = new File(mmFile.parent, xmlFileBasename)
        try {
            xmlFile.withWriter('UTF-8') {
                it << '<!-- vim: set ft=xml: -->\n'
                it << markup
            }
            [_command_after_copying_cstorage_markup, xmlFile.path].execute()
        } catch (RuntimeException e) {
            c.statusInfo = e.message
        }
    }
}
