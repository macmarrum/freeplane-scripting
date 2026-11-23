// SPDX-FileCopyrightText: 2026 macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Edit"})

import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.freeplane.features.map.NodeModel

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.util.List

////// Configuration start //////

// Set it to a full path if need be, e.g. 'C:\\Program Files\\MarkText\\marktext.exe'
final MARKTEXT_COMMAND = 'marktext'

// When set to true, Freeplane will wait for MarkText to exit.
// When false, a dialog will appear for the user to confirm the editing has finished.
final WAIT_FOR_MARKTEXT_TO_EXIT = false

// When more than one Markdown-typed node part is found, the selection buttons will be displayed in this order.
// Remove or comment the ones to be ignored.
final NODE_PARTS_TO_BE_SEARCHED_FOR_MARKDOWN = [
        NodePart.CORE,
        NodePart.DETAILS,
        NodePart.NOTE,
]

// When false, the button mnemonic letter is underlined only when Alt is pressed.
final BUTTON_MNEMONIC_ALWAYS_VISIBLE = true

////// Configuration end //////


node = node as Node
c = c as Controller

final TITLE = 'Edit Markdown in MarkText'
def showConfirmDialog = { String message, int messageType = JOptionPane.ERROR_MESSAGE ->
    NodeModel nodeModel = node.delegate
    UITools.showConfirmDialog(nodeModel, message, TITLE, JOptionPane.CLOSED_OPTION, messageType)
}

static String chooseFromButtons(String title, List<String> labels, Frame owner, boolean mnemonicAlwaysVisible) {
    JDialog dialog = new JDialog(owner, title, true)
    String[] result = [null]

    JPanel panel = new JPanel(new GridLayout(labels.size(), 1, 0, 6))
    panel.border = BorderFactory.createEmptyBorder(12, 12, 12, 12)

    labels.each { String raw ->
        int amp = raw.indexOf('&')
        boolean hasKey = amp >= 0 && amp + 1 < raw.length()

        String plain = hasKey ? raw.substring(0, amp) + raw.substring(amp + 1) : raw
        int keyPos = hasKey ? amp : 0
        char keyChar = hasKey ? raw.charAt(amp + 1) : raw.charAt(0)
        String html
        if (mnemonicAlwaysVisible) {
            String before = plain.substring(0, keyPos)
            String key = plain.substring(keyPos, keyPos + 1)
            String after = plain.substring(keyPos + 1)
            html = "<html>${before}<u>${key}</u>${after}</html>"
        } else {
            html = plain
        }

        JButton button = new JButton(html)
        button.mnemonic = KeyEvent.getExtendedKeyCodeForChar(Character.toUpperCase(keyChar) as int)
        button.displayedMnemonicIndex = keyPos
        button.addActionListener({
            result[0] = plain
            dialog.dispose()
        } as ActionListener)
        panel.add(button)
    }

    JRootPane root = dialog.rootPane
    root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 'cancel')
    root.actionMap.put('cancel', new AbstractAction() {
        @Override
        void actionPerformed(ActionEvent e) {
            dialog.dispose()
        }
    })

    dialog.contentPane.add(panel)
    dialog.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
    dialog.pack()
    dialog.minimumSize = new Dimension(600, dialog.height)
    dialog.locationRelativeTo = owner
    dialog.visible = true  // blocks until disposed

    return result[0]
}

enum NodePart {
    CORE('&CORE'), DETAILS('&DETAILS'), NOTE('NOT&E')

    final String label

    NodePart(String label) {
        this.label = label
    }

    String getLabel() {
        label
    }
}

Map<NodePart, String> orderedNodePartToMarkdown = [:]
NODE_PARTS_TO_BE_SEARCHED_FOR_MARKDOWN.each { nodePart ->
    switch (nodePart) {
        case NodePart.CORE -> if (node.format == 'markdownPatternFormat') orderedNodePartToMarkdown[nodePart] = node.text ?: ''
        case NodePart.DETAILS -> if (node.detailsContentType == 'markdown') orderedNodePartToMarkdown[nodePart] = node.details?.text ?: ''
        case NodePart.NOTE -> if (node.noteContentType == 'markdown') orderedNodePartToMarkdown[nodePart] = node.note?.text ?: ''
    }
}
String text = null
NodePart source = null
if (orderedNodePartToMarkdown.size() == 1) {
    source = orderedNodePartToMarkdown.find { it.value != null }.key
    text = orderedNodePartToMarkdown[source]
} else if (orderedNodePartToMarkdown.size() > 1) {
    List<String> labels = orderedNodePartToMarkdown.collect { it.key.label }
    def choice = chooseFromButtons(TITLE, labels, UITools.currentFrame, BUTTON_MNEMONIC_ALWAYS_VISIBLE)
    if (choice) {
        source = choice as NodePart
        text = orderedNodePartToMarkdown[source]
    }
}
if (!source) {
    c.statusInfo = "No Markdown content — ${TITLE}"
    return
}
def tempFile = File.createTempFile(node.id + '-' + source.name() + '~', '.md')
tempFile.setText(text, 'UTF-8')
final cmdArgs = [MARKTEXT_COMMAND, tempFile]
def editorProcess = cmdArgs.execute()
if (WAIT_FOR_MARKTEXT_TO_EXIT) {
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
