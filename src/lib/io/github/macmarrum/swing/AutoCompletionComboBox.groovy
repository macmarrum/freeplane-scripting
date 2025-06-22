// Copyright (C) 2024, 2025  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
//
// Based on http://www.orbital-computer.de/JComboBox/
// which contains the following notice:
//* This work is hereby released into the Domain.
//* To view a copy of the domain dedication, visit
//* http://creativecommons.org/licenses/publicdomain/
package io.github.macmarrum.swing

import javax.swing.*
import javax.swing.text.AttributeSet
import javax.swing.text.BadLocationException
import javax.swing.text.JTextComponent
import javax.swing.text.PlainDocument
import java.awt.*
import java.awt.event.*
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

class AutoCompletionComboBox extends PlainDocument {
    private JComboBox comboBox
    private Window window
    private ComboBoxModel model
    private JTextComponent editor
    // flag to indicate if setSelectedItem has been called
    // subsequent calls to remove/insertString should be ignored
    private boolean selecting = false
    private boolean hitBackspace = false
    private boolean hitBackspaceOnSelection

    private KeyListener editorKeyListener
    private FocusListener editorFocusListener

    /**
     * Enriches the supplied (editable) JComboBox with auto-completion
     *
     * @param comboBox (mandatory) editable JComboBox to be enriched with auto-completion
     * @param onEntryAccepted (mandatory) closure accepting comboBox as its argument; called before WINDOW_CLOSING is emitted
     * @param window (optional) JFrame or JDialog, where the comboBox is part of; will be closed on Ctrl+L/ENTER or Ctrl+H/ESCAPE
     */
    AutoCompletionComboBox(final Window window, final JComboBox comboBox, final Closure onEntryAccepted) {
        this.window = window
        this.comboBox = comboBox
        model = comboBox.model
        comboBox.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent e) {
                if (!selecting)
                    highlightCompletedText(0)
            }
        })
        comboBox.addPropertyChangeListener(new PropertyChangeListener() {
            void propertyChange(PropertyChangeEvent e) {
                if (e.propertyName == 'editor')
                    configureEditor(e.newValue as ComboBoxEditor)
                if (e.propertyName == 'model')
                    model = e.newValue as ComboBoxModel
            }
        })
        editorKeyListener = new KeyAdapter() {
            void keyPressed(KeyEvent e) {
                if (comboBox.isDisplayable())
                    comboBox.popupVisible = true
                hitBackspace = false
                def isCtrl = (e.modifiersEx & KeyEvent.CTRL_DOWN_MASK) != 0
                switch (e.keyCode) {
                    case KeyEvent.VK_J:
                        comboBox.selectedIndex = comboBox.selectedIndex == comboBox.itemCount - 1 ? 0 : comboBox.selectedIndex + 1
                        break
                    case KeyEvent.VK_K:
                        comboBox.selectedIndex = comboBox.selectedIndex == 0 ? comboBox.itemCount - 1 : comboBox.selectedIndex - 1
                        break
                    case KeyEvent.VK_L:
                    case KeyEvent.VK_ENTER:
                        if (isCtrl || e.keyCode == KeyEvent.VK_ENTER) {
                            onEntryAccepted(comboBox)
                            if (window)
                                window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING))
                        }
                        break
                    case KeyEvent.VK_H:
                    case KeyEvent.VK_ESCAPE:
                        if (isCtrl || e.keyCode == KeyEvent.VK_ESCAPE) {
                            if (window)
                                window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING))
                        }
                        break
                    case KeyEvent.VK_BACK_SPACE:
                        // determine if the pressed key is backspace (needed by the remove method)
                        hitBackspace = true
                        hitBackspaceOnSelection = editor.selectionStart != editor.selectionEnd
                        break
                    case KeyEvent.VK_DELETE:
                        // ignore delete key
                        e.consume()
                        comboBox.toolkit.beep()
                        break
                }
            }
        }
        // Highlight whole text when gaining focus
        editorFocusListener = new FocusAdapter() {
            void focusGained(FocusEvent e) {
                highlightCompletedText(0)
            }
        }
        configureEditor(comboBox.getEditor())
        // Handle initially selected object
        def selected = comboBox.selectedItem
        if (selected != null)
            text = selected.toString()
        highlightCompletedText(0)
    }

    void configureEditor(ComboBoxEditor newEditor) {
        if (editor != null) {
            editor.removeKeyListener(editorKeyListener)
            editor.removeFocusListener(editorFocusListener)
        }

        if (newEditor != null) {
            editor = newEditor.editorComponent as JTextComponent
            editor.addKeyListener(editorKeyListener)
            editor.addFocusListener(editorFocusListener)
            editor.document = this
        }
    }

    void remove(int offs, int len) throws BadLocationException {
        // return immediately when selecting an item
        if (selecting)
            return
        if (hitBackspace) {
            // user hit backspace => move the selection backwards
            // old item keeps being selected
            if (offs > 0) {
                if (hitBackspaceOnSelection)
                    offs--
            } else {
                // User hit backspace with the cursor positioned on the start => beep
                comboBox.toolkit.beep() // when available use: UIManager.getLookAndFeel().provideErrorFeedback(comboBox)
            }
            highlightCompletedText(offs)
        } else {
            super.remove(offs, len)
        }
    }

    void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        // return immediately when selecting an item
        if (selecting)
            return
        // insert the string into the document
        super.insertString(offs, str, a)
        // lookup and select a matching item
        def item = lookupItem(getText(0, length))
        if (item != null) {
            selectedItem = item
        } else {
            // keep old item selected if there is no match
            item = comboBox.selectedItem
            // imitate no insert (later on offs will be incremented by str.length(): selection won't move forward)
            offs = offs - str.length()
            // provide feedback to the user that his input has been received but can not be accepted
            comboBox.toolkit.beep() // when available use: UIManager.getLookAndFeel().provideErrorFeedback(comboBox)
        }
        setText(item.toString())
        // select the completed part
        highlightCompletedText(offs + str.length())
    }

    private void setText(String text) {
        try {
            // remove all text and insert the completed string
            super.remove(0, length)
            super.insertString(0, text, null)
        } catch (BadLocationException e) {
            throw new RuntimeException(e.toString())
        }
    }

    private void highlightCompletedText(int start) {
        editor.setCaretPosition(length)
        editor.moveCaretPosition(start)
    }

    private void setSelectedItem(Object item) {
        selecting = true
        model.selectedItem = item
        selecting = false
    }

    private lookupItem(String pattern) {
        def selectedItem = model.selectedItem
        // only search for a different item if the currently selected does not match
        if (selectedItem != null && startsWithIgnoreCase(selectedItem.toString(), pattern)) {
            return selectedItem
        } else {
            // iterate over all items
            for (int i = 0, n = model.getSize(); i < n; i++) {
                def currentItem = model.getElementAt(i)
                // current item starts with the pattern?
                if (currentItem != null && startsWithIgnoreCase(currentItem.toString(), pattern)) {
                    return currentItem
                }
            }
        }
        // no item starts with the pattern => return null
        return null
    }

    // checks if str1 starts with str2 - ignores case
    private static boolean startsWithIgnoreCase(String str1, String str2) {
        return str1.toUpperCase().startsWith(str2.toUpperCase())
    }
}
