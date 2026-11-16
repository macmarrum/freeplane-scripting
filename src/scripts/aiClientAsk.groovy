// Copyright (C) 2026  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/AI"})

/**
 * askAi from a node, with SystemMessage in branch root and responses as children:
 * <pre>
 * SystemMessage → Prompt → Response [→ P → R ...]
 *                       [→ Response [→ P → R ...]]
 * </pre>
 * Keeps history and chat metadata in attributes.
 * Uses Provider+Model and Effort as selected in Freeplane AI Panel,
 * apiKey and baseUrl as specified in Freeplane AI Preferences.
 */

import groovy.time.TimeCategory
import io.github.macmarrum.swing.AiClient
import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.resources.ResourceController
import org.freeplane.core.ui.components.UITools
import org.freeplane.core.ui.sounds.SoundClipPlayer
import org.freeplane.features.map.NodeModel
import org.freeplane.plugin.script.FreeplaneScriptBaseClass

import javax.swing.JOptionPane
import javax.swing.Timer

final SYSTEM_MESSAGE_STYLE = '-SystemMessage'
final GROUPING_STYLE = null
final PROMPT_STYLE = '-Prompt'
final RESPONSE_STYLE = '-Response'

// Note: Prompt is always recorded in node[latestModifiedAt]
final REMOVE_PROMPT_FROM_NOTE_OR_CORE = false

final MOVE_SUBSEQUENT_PROMPT_TO_NOTE_IF_CORE_IS_LONG = true
final MINIMIZE_PROMPT_NODE_IF_CORE_IS_LONG = true

// Note: Put ai-response-success.wav and/or ai-response-error.wav in <user-dir>/resources/sounds/
final SUCCESSFUL_RESPONSE_SOUND_WAV_STEM = 'ai-response-success'
final ERROR_RESPONSE_SOUND_WAV_STEM = 'ai-response-error'

c = c as Controller
node = node as Node
FreeplaneScriptBaseClass.ConfigProperties config = config

def showError = { String message, int messageType = JOptionPane.ERROR_MESSAGE ->
    NodeModel nodeModel = node.delegate
    UITools.showConfirmDialog(nodeModel, message, 'Ai Ask With Config', JOptionPane.CLOSED_OPTION, messageType)
}

if (node.style.name !in [null, PROMPT_STYLE]) {
    showError("The node already has a style different than '${PROMPT_STYLE}'")
    return
}
node.style.name = PROMPT_STYLE

def properPathToRoot = node.pathToRoot.reverse()
def properPathToSysMsg = new ArrayList<Node>()
Node systemMessageNode = null
for (n in properPathToRoot) {
    properPathToSysMsg << n
    if (n.style.name == SYSTEM_MESSAGE_STYLE) {
        systemMessageNode = n
        break
    }
}
if (!systemMessageNode) {
    showError("No system-message node found in pathToRoot\nExpected a node with the style ${SYSTEM_MESSAGE_STYLE}")
    return
}

def getText = { Node n ->
    def text = switch (n.style.name) {
        case SYSTEM_MESSAGE_STYLE -> n.note?.text ?: n.text
        case null -> n.note?.text ?: n.text
        case PROMPT_STYLE -> {
            def prompt = n.note?.text ?: n.text
            if (prompt) {
                prompt
            } else {
                def previousModifiedAt = n['modified_at'].text
                previousModifiedAt ? n[previousModifiedAt].text : null
            }
        }
        case RESPONSE_STYLE -> n.note?.text ?: n.text
        default -> {
            def msg = "Unexpected node style (${n.style.name})\nExpected one of ${[SYSTEM_MESSAGE_STYLE, null, PROMPT_STYLE, RESPONSE_STYLE]}"
            throw new IllegalArgumentException(msg)
        }
    }
    if (!text) {
        throw new IllegalArgumentException("No message found in node ${n.id}")
    }
    return text
}

def printNodePathData = { List<Node> nodePath ->
    nodePath.each {
        print "${it.id}: ${it.style.name}\n${getText(it).indent(2)}"
    }
}

def messages = []
def pathFromSysMsg = properPathToSysMsg.reverse()
def chatNodePath = pathFromSysMsg.findAll { it.style.name in [SYSTEM_MESSAGE_STYLE, PROMPT_STYLE, RESPONSE_STYLE] || it.id == node.id }
chatNodePath.eachWithIndex { Node n, int i ->
    if (i == 0) {
        messages << getText(n)
    } else if (i % 2 != 0) {
        if (n.style.name == PROMPT_STYLE) {
            messages << getText(n)
        } else {
            def msg = "Unsupported style for ${n.id}: ${n.style.name}\nExpected ${PROMPT_STYLE}"
            println msg
            printNodePathData(chatNodePath)
            throw new IllegalArgumentException(msg)
        }
    } else {
        if (n.style.name == RESPONSE_STYLE) {
            messages << getText(n)
        } else {
            def msg = "Unsupported style for ${n.id}: ${n.style.name}\nExpected ${RESPONSE_STYLE}"
            println msg
            printNodePathData(chatNodePath)
            throw new IllegalArgumentException(msg)
        }
    }
}


def ISO_DATE_FMT = 'yyyy-MM-dd HH:mm:ss'

def getNow = {
    def dt = new Date()
    return [dt, dt.format(ISO_DATE_FMT)]
}

def getIsoNow = {
    return new Date().format(ISO_DATE_FMT)
}

def parentStyle = node.parent.style.name
def expectedParentStyles = [SYSTEM_MESSAGE_STYLE, RESPONSE_STYLE, GROUPING_STYLE, null]
if (parentStyle !in expectedParentStyles) {
    showError("Unsupported style of parent: ${parentStyle}\nExpected parent with style in ${expectedParentStyles}")
    return
}

def (promptDate, promptIsoDate) = getNow()
def previousModifiedAt = node['modified_at'].text
if (previousModifiedAt) {
    def previousMessage = node[previousModifiedAt].text
    if (previousMessage == messages.last()) {
        node[previousModifiedAt] = null
    }
}
node['modified_at'] = promptIsoDate
node[promptIsoDate] = messages.last()

def isCoreLong = {
    return node.text.size() > config.getIntProperty('max_shortened_text_length')
}

if (REMOVE_PROMPT_FROM_NOTE_OR_CORE) {
    if (node.note) {
        node.note = null
    } else {
        node.text = null
    }
}
if (MOVE_SUBSEQUENT_PROMPT_TO_NOTE_IF_CORE_IS_LONG && messages.size() >= 4 && !node.note && isCoreLong()) {
    node.note = node.text
    node.text = null
}
if (MINIMIZE_PROMPT_NODE_IF_CORE_IS_LONG && isCoreLong()) {
    node.minimized = true
}

def (provider, model) = config.getProperty('ai_selected_model').split(/\|/, 2)
def effort = config.getProperty('ai_thinking_effort')

final PROVIDERS = ['openai', 'openrouter', 'requesty', 'custom', 'gemini', 'ollama']
String baseUrl = switch (provider) {
    case PROVIDERS -> config.getProperty("ai_${provider}_service_address")
    default -> null
}
String apiKey = switch (provider) {
    case PROVIDERS -> config.getProperty("ai_${provider}_key")
    default -> null
}

def responseNode = node.createChild("...running since ${getIsoNow()}")
responseNode['modified_at'] = promptIsoDate
responseNode['model'] = "${provider}/${model}"
responseNode['effort'] = effort
responseNode.style.name = RESPONSE_STYLE
c.select(responseNode)

new AiClient(provider: provider, baseUrl: baseUrl, apiKey: apiKey, model: model, effort: effort).chatAsync(messages)
        {
            def (responseDate, responseIsoDate) = getNow()
            responseNode.text = null
            responseNode.note = it.aiMessage().text()
            responseNode['modified_at'] = responseIsoDate
            responseNode['input_token_count'] = it.tokenUsage().inputTokenCount()
            responseNode['output_token_count'] = it.tokenUsage().outputTokenCount()
            responseNode['duration'] = TimeCategory.minus(responseDate, promptDate).toString()
            playSound(SUCCESSFUL_RESPONSE_SOUND_WAV_STEM)
            flashSpotlight()
        }
        {
            def (responseDate, responseIsoDate) = getNow()
            responseNode.text = null
            responseNode.note = '''=node['error']'''
            responseNode['modified_at'] = responseIsoDate
            responseNode['error'] = it.message
            responseNode['duration'] = TimeCategory.minus(responseDate, promptDate).toString()
            playSound(ERROR_RESPONSE_SOUND_WAV_STEM)
            flashSpotlight()
        }

static playSound(String soundWavStem) {
    // org.freeplane.core.ui.sounds.SoundClipPlayer.loadSoundClip
    ResourceController resourceController = ResourceController.getResourceController()
    final propertyName = 'playsSound.' + soundWavStem
    final oldValue = resourceController.getBooleanProperty(propertyName)
    resourceController.setProperty(propertyName, true)
    SoundClipPlayer.playSound(soundWavStem)
    resourceController.setProperty(propertyName, oldValue)
}

static void toggleSpotlight() {
    // org.freeplane.features.styles.SetBooleanMapViewPropertyAction.actionPerformed
    def propertyName = 'spotlight'
    def mapViewComponent = org.freeplane.features.mode.Controller.getCurrentController().getMapViewManager().getMapViewComponent()
    final Boolean value = Boolean.TRUE.equals(mapViewComponent.getClientProperty(propertyName))
    boolean newValue = !value.booleanValue()
    mapViewComponent.putClientProperty(propertyName, newValue)
}

static void flashSpotlight() {
    toggleSpotlight()
    // Timer executes its listeners on the Event Dispatch Thread (EDT), which is crucial for Swing's thread safety
    new Timer(400, { evt -> toggleSpotlight(); evt.source.stop() }).start()
}
