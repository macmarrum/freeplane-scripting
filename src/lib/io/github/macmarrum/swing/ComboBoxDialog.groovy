// Copyright (C) 2024, 2025  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.macmarrum.swing

import javax.swing.*
import javax.swing.border.EmptyBorder
import java.awt.*
import java.awt.event.ItemEvent
import java.awt.event.WindowEvent

class ComboBoxDialog {
    JDialog dialog
    JComboBox comboBox
    JCheckBox checkBox

    /**
     * JDialog with an editable comboBox
     *
     * @param parent component for {@code locationRelativeTo}, can be null
     * @param comboboxEnricher {@link ComboBox} (default) or {@link AutoCompletionComboBox}
     * @param title window title
     * @param elements an array of combobox elements
     * @param onEntryAccepted closure accepting comboBox, checkBox as its arguments - see {@link ComboBox}, {@link AutoCompletionComboBox}
     * @param message (optional) text above combobox
     * @param checkboxText (optional) checkbox above message
     * @param onCheckboxToggled closure accepting comboBox, checkBox as its arguments - see {@link ComboBox}, {@link AutoCompletionComboBox}
     */
    ComboBoxDialog(final Component parent, Class comboboxEnricher = null, final String title, final String[] elements, Closure onEntryAccepted, final String message = null, final String checkboxText = null, Closure onCheckboxToggled = null) {
        dialog = new JDialog()
        comboBox = new JComboBox<String>(elements)
        comboBox.editable = true // required for keyListener, which relies on comboBox having its editor component

        dialog.locationRelativeTo = parent
        dialog.title = title
        dialog.defaultCloseOperation = JDialog.DISPOSE_ON_CLOSE
        dialog.contentPane.layout = new BorderLayout(10, 10)
        dialog.contentPane.border = new EmptyBorder(13, 10, 10, 10)

        if (checkboxText) {
            checkBox = new JCheckBox(checkboxText)
            // enrich checkBox with itemListener (click)
            checkBox.addItemListener { ItemEvent e ->
                // action it, irrespective of e.stateChange == ItemEvent.SELECTED
                onCheckboxToggled(comboBox, checkBox)
                dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING))
            }
            setupMnemonic(checkBox, checkboxText)
            dialog.contentPane.add(checkBox, BorderLayout.NORTH)
        }
        if (message) {
            def label = new JLabel(message)
            dialog.contentPane.add(label, BorderLayout.CENTER)
        }
        // make width a bit longer than the longest entry
        comboBox.prototypeDisplayValue = elements.max { it.size() } + 'xxxxx'
        dialog.contentPane.add(comboBox, BorderLayout.SOUTH)
        dialog.pack()
        comboBox.requestFocusInWindow()
        def onEntryAcceptedWithoutCheckBox = { JComboBox _comboBox ->
            // enrich callback with a checkBox
            onEntryAccepted(_comboBox, checkboxText ? checkBox : null)
        }
        // enrich comboBox
        if (comboboxEnricher == null)
            comboboxEnricher = ComboBox.class
        def enricherConstructor = comboboxEnricher.getConstructor(Window.class, JComboBox.class, Closure.class)
        def _enricher = enricherConstructor.newInstance(dialog, comboBox, onEntryAcceptedWithoutCheckBox)
        dialog.visible = true
    }

    /**
     * Parses a label string for a mnemonic indicator ('&') and sets the mnemonic and displayed mnemonic index on the button
     *
     * @param button AbstractButton to set the mnemonic on
     * @param text label, possibly containing '&' to indicate the mnemonic; use '&&' for a literal ampersand
     */
    static void setupMnemonic(AbstractButton button, String text) {
        // prepare for replacement of (escaped &) `&&` with `&`
        def doubleAmpersandArray = text.split('&&')
        def finalLengthText = doubleAmpersandArray.join('#')
        int mnemonicIndex = finalLengthText.indexOf('&')
        if (mnemonicIndex != -1 && mnemonicIndex < text.length() - 1) {
            // Remove the single `&` used for mnemonic indication and put back `&` where `&&` was
            button.text = doubleAmpersandArray.collect { it.replace('&', '') }.join('&')
            button.mnemonic = finalLengthText[mnemonicIndex + 1]
            button.displayedMnemonicIndex = mnemonicIndex
        } else {
            // No mnemonic found, or '&' is at the end
            button.text = text.replace('&&', '&')
            button.mnemonic = 0 // Clear any existing mnemonic
            button.displayedMnemonicIndex = -1 // Clear any existing index
        }
    }
}

class ComboBoxDialogBuilder {
    private Component parent
    private String title
    private String[] elements
    private Closure onEntryAccepted
    private String message
    private String checkboxText
    private Closure onCheckBobToggled
    private Class comboboxClass

    ComboBoxDialogBuilder withParent(Component parent) {
        this.parent = parent
        return this
    }

    ComboBoxDialogBuilder withComboboxClass(Class comboboxClass) {
        this.comboboxClass = comboboxClass
        return this
    }

    ComboBoxDialogBuilder withTitle(String title) {
        this.title = title
        return this
    }

    ComboBoxDialogBuilder withElements(String... elements) {
        this.elements = elements
        return this
    }

    ComboBoxDialogBuilder withOnEntryAccepted(Closure onEntryAccepted) {
        this.onEntryAccepted = onEntryAccepted
        return this
    }

    ComboBoxDialogBuilder withMessage(String message) {
        this.message = message
        return this
    }

    ComboBoxDialogBuilder withCheckboxText(String checkboxText) {
        this.checkboxText = checkboxText
        return this
    }

    ComboBoxDialogBuilder withOnCheckBoxToggled(Closure onCheckboxToggled) {
        this.onCheckBobToggled = onCheckboxToggled
        return this
    }

    ComboBoxDialog build() {
        return new ComboBoxDialog(parent, comboboxClass, title, elements, onEntryAccepted, message, checkboxText, onCheckBobToggled)
    }
}
