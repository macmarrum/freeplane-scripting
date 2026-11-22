// SPDX-FileCopyrightText: 2022, 2026 macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Edit"})
import org.freeplane.api.Node

node = node as Node
def tempFile = File.createTempFile(node.id + '~', '.tmp')
def text = node.text
tempFile.setText(text, 'UTF-8')
def editorProcess = ['gvim', '--nofork', tempFile].execute()
// def editorProcess = ['C:/Program Files/Notepad++/notepad++.exe', '-nosession', '-notabbar', '-multiInst', tempFile].execute()
editorProcess.waitFor()
def newText = tempFile.getText('UTF-8')
if (newText != text)
    node.text = newText
tempFile.delete()
