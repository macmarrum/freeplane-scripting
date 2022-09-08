// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac2"})
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
