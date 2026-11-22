// Copyright (C) 2026  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Edit"})


import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.freeplane.features.map.NodeModel

import javax.swing.*
import java.awt.*
import java.awt.event.ActionListener
import java.util.List

node = node as Node
c = c as Controller

enum NodePart {
    CORE, DETAILS, NOTE
}

////// Configuration start //////

// Set it to a full path if need be, e.g. 'C:\\Program Files\\MarkText\\marktext.exe'
final MARKTEXT_COMMAND = 'marktext'

// When set to true, Freeplane will wait for MarkText to exit.
// When false, a dialog will appear to confirm the editing has finished.
final ASSUME_SINGLE_PROCESS_MARKTEXT = false

////// Configuration end //////


final TITLE = 'Edit Markdown in MarkText'
def showConfirmDialog = { String message, int messageType = JOptionPane.ERROR_MESSAGE ->
    NodeModel nodeModel = node.delegate
    UITools.showConfirmDialog(nodeModel, message, TITLE, JOptionPane.CLOSED_OPTION, messageType)
}

static String chooseFromButtons(String title, List<String> options, Frame owner) {
    JDialog dialog = new JDialog(owner, title, true)
    String[] result = [null]

    JPanel panel = new JPanel(new GridLayout(options.size(), 1, 0, 6))
    panel.border = BorderFactory.createEmptyBorder(12, 12, 12, 12)

    options.each { String label ->
        JButton button = new JButton(label)
        button.addActionListener({
            result[0] = label
            dialog.dispose()
        } as ActionListener)
        panel.add(button)
    }

    dialog.contentPane.add(panel)
    dialog.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
    dialog.pack()
    dialog.minimumSize = new Dimension(600, dialog.height)
    dialog.locationRelativeTo = owner
    dialog.visible = true  // blocks until disposed

    return result[0]
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
if (nodePartToMarkdown.size() == 1) {
    source = nodePartToMarkdown.find { it.value }.key
    text = nodePartToMarkdown[source]
} else if (nodePartToMarkdown.size() > 1) {
    List<String> options = nodePartToMarkdown.findAll { it.value }.collect { it.key.name() }
    def label = chooseFromButtons(TITLE, options, UITools.currentFrame)
    if (label) {
        source = label as NodePart
        text = nodePartToMarkdown[source]
    }
}
if (!text || !source) {
    c.statusInfo = 'No Markdown content'
    return
}
def tempFile = File.createTempFile(node.id + '-' + source.name() + '~', '.md')
tempFile.setText(text, 'UTF-8')
final cmdArgs = [MARKTEXT_COMMAND, tempFile]
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
