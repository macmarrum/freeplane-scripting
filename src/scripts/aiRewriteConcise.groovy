// Copyright (C) 2026  macmarrum (at) outlook (dot) ie
// SPDX-License-Identifier: GPL-3.0-or-later
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/AI"})

import org.freeplane.api.Controller
import org.freeplane.api.ai.AiModelSelection
import org.freeplane.api.ai.AiRequestMode
import org.freeplane.api.ai.AiRequestOptions
import org.freeplane.api.ai.AiRequestStatus
import org.freeplane.api.ai.AiToolAvailability
import org.freeplane.plugin.ai.tools.MessageBuilder
import org.freeplane.plugin.script.FreeplaneScriptBaseClass

import java.time.Duration

c = c as Controller
def config = new FreeplaneScriptBaseClass.ConfigProperties()
def node = c.selected

def systemMessage = 'You are an expert editor specializing in making text clearer, more concise, and more impactful. Your task is to rewrite the following text while maintaining the original meaning and intent, and to change only what is necessary.'
def providerModel = ['Ollama', 'phi4-mini:latest']

def originalSystemMessage = config.getProperty(MessageBuilder.SYSTEM_MESSAGE_PROPERTY)
config.setProperty(MessageBuilder.SYSTEM_MESSAGE_PROPERTY, systemMessage)

def prompt = node.text
def aiRequestOptions = AiRequestOptions.builder()
        .modelSelection(AiModelSelection.explicit(*providerModel))
        .toolAvailability(AiToolAvailability.DISABLED)
        .mode(AiRequestMode.SHOW_IN_CHAT)
        .timeout(Duration.ofSeconds(60))
        .build()
c.askAi(prompt, aiRequestOptions) { result ->
    if (result.status == AiRequestStatus.SUCCEEDED) {
        node.text = result.response
        node.details = "askAi prompt: ${prompt}"
    } else {
        node.details = "askAi status: ${result.status.name()}"
    }
    node.note = result.detail
}
config.setProperty(MessageBuilder.SYSTEM_MESSAGE_PROPERTY, originalSystemMessage)
