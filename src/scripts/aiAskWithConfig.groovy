// Copyright (C) 2026  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/AI"})
import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.api.ai.AiRequestMode
import org.freeplane.api.ai.AiRequestOptions
import org.freeplane.api.ai.AiRequestStatus
import org.freeplane.api.ai.AiToolAvailability
import org.freeplane.core.ui.components.UITools
import org.freeplane.features.map.NodeModel
import org.freeplane.plugin.script.FreeplaneScriptBaseClass

import javax.swing.JOptionPane
import java.time.Duration

SYSTEM_MESSAGE_STYLE = '-SystemMessage'
PROMPT_STYLE = '-Prompt'
RESPONSE_STYLE = '-Response'

c = c as Controller
node = node as Node
FreeplaneScriptBaseClass.ConfigProperties config = config

def showError = { String message ->
    NodeModel nodeModel = node.delegate
    UITools.showConfirmDialog(nodeModel, message, 'Ai Ask With Config', JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE)
}

if (node.style.name !in [null, PROMPT_STYLE]) {
    showError("The node already has a style different than '${PROMPT_STYLE}'")
    return
}
node.style.name = PROMPT_STYLE

def systemMessageNode = node.pathToRoot.reverse().find { it.style.name == SYSTEM_MESSAGE_STYLE }
if (!systemMessageNode) {
    c.statusInfo = 'No system-message node found in pathToRoot'
}
def systemMessage = systemMessageNode?.text ?: config.getProperty('ai_system_message')

def userMessage = node.note?.plain ?: node.text
def (provider, model) = config.getProperty('ai_selected_model').split(/\|/, 2)
def effort = config.getProperty('ai_thinking_effort')
String error

static String getIsoNow() {
    new Date().format('yyyy-MM-dd HH:mm:ss')
}

def isoNow = getIsoNow()
node['modified_at'] = isoNow
node[isoNow] = userMessage

if (node.parent.style.name == RESPONSE_STYLE && !node.isLeaf()) {
    showError('Mid-conversation branching is not supported')
    return
}

// Note: no model or effort configuration — uses what's selected in the UI
def aiRequestOptions = AiRequestOptions.builder()
        .exactSystemMessage(systemMessage)
        .toolAvailability(AiToolAvailability.DISABLED)
        .mode(node.parent.style.name == RESPONSE_STYLE ? AiRequestMode.ADD_TO_CHAT : AiRequestMode.SHOW_IN_NEW_CHAT)
        .timeout(Duration.ofSeconds(60 * 3))
        .build()
c.askAi(userMessage, aiRequestOptions) { result ->
    Node responseNode
    if (result.status == AiRequestStatus.SUCCEEDED) {
        responseNode = node.createChild(result.response)
        responseNode.style.name = RESPONSE_STYLE
    } else {
        responseNode = node.createChild()
        error = "${result.status.name()}\n${result.detail}".replaceAll(/[ \n]+$/, '')
    }
    responseNode['modified_at'] = getIsoNow()
    responseNode['model'] = "${provider}/${model}"
    responseNode['effort'] = effort
    if (error) responseNode['error'] = error
}
