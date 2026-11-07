// Copyright (C) 2026  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/AI"})

/**
 * askAi from a node, with SystemMessage in branch root and responses as children:
 * <pre>
 * SystemMessage → Prompt → Response [→ P → R ...]
 *                       [→ Response [→ P → R ...]]
 * </pre>
 * Keeps history and chat metadata in nodes.
 * Uses Provider+Model and Effort as selected in Freeplane AI Panel;
 * apiKey and baseUrl from Freeplane AI Preferences.
 */

import groovy.time.TimeCategory
import io.github.macmarrum.swing.AiClient
import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.freeplane.features.map.NodeModel
import org.freeplane.plugin.script.FreeplaneScriptBaseClass

import javax.swing.JOptionPane

SYSTEM_MESSAGE_STYLE = '-SystemMessage'
GROUPING_STYLE = null
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
    c.statusInfo = 'No system-message node found in pathToRoot'
}

static getText(Node n) {
    return n.note?.text ?: n.text
}

void printNodePathData(List<Node> nodePath) {
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
node['modified_at'] = promptIsoDate
node[promptIsoDate] = messages.last()

def (provider, model) = config.getProperty('ai_selected_model').split(/\|/, 2)
def effort = config.getProperty('ai_thinking_effort')

String baseUrl = switch (provider) {
    case 'gemini' -> config.getProperty('ai_gemini_service_address')
    case 'openrouter' -> config.getProperty('ai_openrouter_service_address')
    case 'ollama' -> config.getProperty('ai_ollama_service_address')
    default -> null
}
String apiKey = switch (provider) {
    case 'gemini' -> config.getProperty('ai_gemini_key')
    case 'openrouter' -> config.getProperty('ai_openrouter_key')
    default -> null
}

def responseNode = node.createChild("...running since ${getIsoNow()}")
responseNode['modified_at'] = promptIsoDate
responseNode['model'] = "${provider}/${model}"
responseNode['effort'] = effort
responseNode.style.name = RESPONSE_STYLE

new AiClient(provider: provider, baseUrl: baseUrl, apiKey: apiKey, model: model, effort: effort).chatAsync(messages)
        {
            def (responseDate, responseIsoDate) = getNow()
            responseNode['modified_at'] = responseIsoDate
            responseNode.text = it.aiMessage().text()
            responseNode['input_token_count'] = it.tokenUsage().inputTokenCount()
            responseNode['output_token_count'] = it.tokenUsage().outputTokenCount()
            responseNode['duration'] = TimeCategory.minus(responseDate, promptDate).toString()
        }
        {
            def (responseDate, responseIsoDate) = getNow()
            responseNode['modified_at'] = responseIsoDate
            responseNode.text = null
            responseNode['error'] = it.message
            responseNode['duration'] = TimeCategory.minus(responseDate, promptDate).toString()
        }
