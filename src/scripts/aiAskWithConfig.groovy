// Copyright (C) 2026  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/AI"})
import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.api.ai.AiModelConfiguration
import org.freeplane.api.ai.AiRequestMode
import org.freeplane.api.ai.AiRequestOptions
import org.freeplane.api.ai.AiRequestStatus
import org.freeplane.api.ai.AiThinkingEffort
import org.freeplane.api.ai.AiToolAvailability

import java.time.Duration

c = c as Controller
node = node as Node

def systemMessage = node.parent.text ?: config.getProperty('ai_system_message')
def userMessage = node.note?.plain ?: node.text

def aiRequestOptions = AiRequestOptions.builder()
        // no model configuration — use what's selected in the UI
        .exactSystemMessage(systemMessage)
        .toolAvailability(AiToolAvailability.DISABLED)
        .mode(AiRequestMode.SHOW_IN_NEW_CHAT)
        .timeout(Duration.ofSeconds(60 * 3))
        .build()
c.askAi(userMessage, aiRequestOptions) { result ->
    Node resultNode
    if (result.status == AiRequestStatus.SUCCEEDED) {
        resultNode = node.createChild(result.response)
        def ncs = resultNode.conditionalStyles
        ncs.add(true, null, '=Markdown', false)
        ncs.add(true, null, '+max20cm', false)
    } else {
        resultNode = node.createChild()
        resultNode.details = "askAi status: ${result.status.name()}"
    }
    resultNode.note = result.detail
}
