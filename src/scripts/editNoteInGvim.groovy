// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
def tempFile = File.createTempFile(node.id + '~', '.tmp')
String text = node.note ?: ''
tempFile.setText(text, 'UTF-8')
def editorProcess = ['gvim', '--nofork', tempFile].execute()
// def editorProcess = ['C:/Program Files/Notepad++/notepad++.exe', '-nosession', '-notabbar', '-multiInst', tempFile].execute()
editorProcess.waitFor()
def newText = tempFile.getText('UTF-8')
if (newText != text)
    node.note = newText ?: null
tempFile.delete()
