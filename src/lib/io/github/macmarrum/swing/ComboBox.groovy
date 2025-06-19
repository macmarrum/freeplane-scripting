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

    ComboBox(final Window window, final JComboBox comboBox, final Closure onEntryAccepted) {
        this.window = window
        this.comboBox = comboBox
        comboBox.editable = true

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
                            e.consume() // Consume the event to prevent it from propagating further (e.g., newline in editor)
                            onEntryAccepted(comboBox)
                            if (window)
                                window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING))
                        }
                        break
                    case KeyEvent.VK_H:
                    case KeyEvent.VK_ESCAPE:
                        if (isCtrl || e.keyCode == KeyEvent.VK_ESCAPE) {
                            e.consume() // Consume ESCAPE to prevent default behavior
                            if (window)
                                window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING))
                        }
                        break
                }
            }
        }

        def editorComponent = comboBox.getEditor().getEditorComponent()
        editorComponent.addKeyListener(editorKeyListener)

        if (window != null) {
            window.visible = true
        }
    }
}
