// Copyright (C) 2024, 2025  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.macmarrum.swing

import javax.swing.*
import javax.swing.border.EmptyBorder
import java.awt.*

class ComboBoxDialog {
    JDialog dialog
    JComboBox comboBox

    /**
     * JDialog with a combobox
     *
     * @param parent (optional) component for {@code locationRelativeTo}
     * @param title window title
     * @param message (optional) text above combobox
     * @param elements an array of combobox elements
     * @param onEntryAccepted closure accepting comboBox as its argument - see {@link AutoCompletionComboBox}
     * @param autocompletion use AutoCompletionComboBox, otherwise ComboBox
     */
    ComboBoxDialog(final Component parent = null, final String title, final String message = null, final String[] elements, final Closure onEntryAccepted, final boolean autocompletion = false) {
        dialog = new JDialog()
        comboBox = new JComboBox<String>(elements)
        dialog.locationRelativeTo = parent
        dialog.title = title
        dialog.defaultCloseOperation = JDialog.DISPOSE_ON_CLOSE
        dialog.contentPane.layout = new BorderLayout(10,10)
        dialog.contentPane.border = new EmptyBorder(13,10,10,10)
        if (message) {
            def label = new JLabel(message)
            dialog.contentPane.add(label, BorderLayout.NORTH)
        }
        // make width a bit longer than the longest entry
        comboBox.prototypeDisplayValue = elements.max { it.size() } + 'xxxxx'
        dialog.contentPane.add(comboBox, BorderLayout.CENTER)
        dialog.pack()
        if (autocompletion)
            def cb = new AutoCompletionComboBox(dialog, comboBox, onEntryAccepted)
        else
            def cb = new ComboBox(dialog, comboBox, onEntryAccepted)
    }
}
