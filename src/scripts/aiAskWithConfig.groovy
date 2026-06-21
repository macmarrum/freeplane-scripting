// Copyright (C) 2026  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/AI"})


import org.freeplane.api.Controller
import org.freeplane.api.ai.*

import java.time.Duration

c = c as Controller
def node = c.selected

//def providerModel = ['ollama', 'phi4-mini:latest']
//def providerModel = ['OpenRouter', 'nex-agi/nex-n2-pro:free']
def providerModel = config.getProperty('ai_selected_model').split('\\|', 2)

def systemMessage = node.parent.text ?: null  // fall back to default if no parent text
def userMessage = node.text

def aiRequestOptions = AiRequestOptions.builder()
        .systemMessage(systemMessage)
        .modelSelection(AiModelSelection.explicit(*providerModel))
        .toolAvailability(AiToolAvailability.DISABLED)
        .mode(AiRequestMode.SHOW_IN_NEW_CHAT)
        .timeout(Duration.ofSeconds(60))
        .build()
//println("${new Date().format('HH:mm:ss')} askAi: ${userMessage} | ${aiRequestOptions.mode.name()}")
c.askAi(userMessage, aiRequestOptions) { result ->
    if (result.status == AiRequestStatus.SUCCEEDED) {
        node.text = result.response
        node.details = userMessage
    } else {
        node.details = "askAi status: ${result.status.name()}"
    }
    node.note = result.detail
}
