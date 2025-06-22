// Copyright (C) 2025  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later

package io.github.macmarrum.swing

import javax.swing.*
import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.WindowEvent

class ComboBox {
    private JComboBox comboBox
    private Window window

    /**
     * Enriches the supplied (editable) JComboBox with a keyListener
     *
     * @param comboBox (mandatory) editable JComboBox to be enriched with auto-completion
     * @param onEntryAccepted (mandatory) closure accepting comboBox as its argument; called before WINDOW_CLOSING is emitted
     * @param window (optional) JFrame or JDialog, where the comboBox is part of; will be closed on Ctrl+L/ENTER or Ctrl+H/ESCAPE
     */
    ComboBox(final Window window, final JComboBox comboBox, final Closure onEntryAccepted) {
        this.window = window
        this.comboBox = comboBox

        def editorKeyListener = new KeyAdapter() {
            void keyPressed(KeyEvent e) {
                if (comboBox.isDisplayable())
                    comboBox.popupVisible = true
                boolean isCtrl = (e.modifiersEx & KeyEvent.CTRL_DOWN_MASK) != 0
                switch (e.keyCode) {
                    case KeyEvent.VK_J:
                        if (isCtrl)
                            comboBox.selectedIndex = (comboBox.selectedIndex != comboBox.itemCount - 1) ? comboBox.selectedIndex + 1 : 0
                        break
                    case KeyEvent.VK_K:
                        if (isCtrl)
                            comboBox.selectedIndex = (comboBox.selectedIndex != 0) ? comboBox.selectedIndex - 1 : comboBox.itemCount - 1
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
                }
            }
        }

        def editorComponent = comboBox.getEditor().getEditorComponent()
        editorComponent.addKeyListener(editorKeyListener)
    }
}
