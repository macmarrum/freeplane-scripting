// Copyright (C) 2026  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Edit"})

import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.freeplane.features.map.NodeModel

import javax.swing.JOptionPane

node = node as Node
c = c as Controller

enum NodePart {
    CORE, DETAILS, NOTE
}

// Configuration start

// When set to true, Freeplane will wait for MarkText to exit.
// When false, a dialog will appear to confirm the editing has finished.
final ASSUME_SINGLE_PROCESS_MARKTEXT = false

// Markdown can be located in core, note or details. This sets the priority for editing.
final PRIORITY = [
        NodePart.CORE,
        NodePart.NOTE,
        NodePart.DETAILS,
]
// Configuration end

def showConfirmDialog = { String message, int messageType = JOptionPane.ERROR_MESSAGE ->
    NodeModel nodeModel = node.delegate
    UITools.showConfirmDialog(nodeModel, message, 'Edit Markdown in MarkText', JOptionPane.CLOSED_OPTION, messageType)
}

Map<NodePart, String> nodePartToMarkdown = [:]
if (node.format == 'markdownPatternFormat') {
    def core = node.text
    if (core) nodePartToMarkdown[NodePart.CORE] = core
}
if (node.noteContentType == 'markdown') {
    def note = node.note?.text
    if (note) nodePartToMarkdown[NodePart.NOTE] = note
}
if (node.detailsContentType == 'markdown') {
    def details = node.details?.text
    if (details) nodePartToMarkdown[NodePart.DETAILS] = details
}

String text = null
NodePart source = null
for (nodePart in PRIORITY) {
    text = nodePartToMarkdown.get(nodePart)
    if (text) {
        source = nodePart
        break
    }
}
if (!text) {
    c.statusInfo = 'No Markdown content'
    return
}
def tempFile = File.createTempFile(node.id + '-' + source.name() + '~', '.md')
tempFile.setText(text, 'UTF-8')
final cmdArgs = ['marktext', tempFile]
def editorProcess = cmdArgs.execute()
if (ASSUME_SINGLE_PROCESS_MARKTEXT) {
    editorProcess.waitFor()
} else {
    showConfirmDialog('Once you have finished in MarkText (saved and/or closed the temporary file), click OK.', JOptionPane.INFORMATION_MESSAGE)
}
def newText = tempFile.getText('UTF-8')
if (newText != text) {
    switch (source) {
        case NodePart.CORE -> node.text = newText
        case NodePart.NOTE -> node.note = newText
        case NodePart.DETAILS -> node.details = newText
    }
}
tempFile.delete()
