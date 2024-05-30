/**
 * Copyright (C) 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package io.github.macmarrum.swing

import javax.swing.*
import java.awt.*

class AutoCompletionComboDialog {
    JDialog dialog
    JComboBox comboBox

    /**
     * JDialog with an auto-completion combobox
     *
     * @param parent (optional) component for {@code locationRelativeTo}
     * @param title window title
     * @param elements an array of combobox elements
     * @param onEntryAccepted closure accepting comboBox as its argument - see {@link AutoCompletionComboBox}
     */
    AutoCompletionComboDialog(final Component parent, final String title, final String[] elements, final Closure onEntryAccepted) {
        this.dialog = new JDialog()
        this.comboBox = new JComboBox<String>(elements)
        dialog.locationRelativeTo = parent
        dialog.title = title
        dialog.defaultCloseOperation = JDialog.DISPOSE_ON_CLOSE
        dialog.layout = new FlowLayout(FlowLayout.LEFT, 10, 10)
        // make width a bit longer than the longest entry
        comboBox.prototypeDisplayValue = elements.max { it.size() } + 'xxxxx'
        dialog.contentPane.add(comboBox)
        dialog.pack()
        def accb = new AutoCompletionComboBox(this.dialog, this.comboBox, onEntryAccepted)
    }
}
