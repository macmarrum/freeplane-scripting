/*
 * Copyright (C) 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/AI"})

import io.github.macmarrum.swing.AiClient

node = node as org.freeplane.api.Node

String model = config.getProperty('ai_selected_model')
String key = model.startsWith('openrouter|') ? config.getProperty('ai_openrouter_key') : null // default, i.e. none for ollama
String url = model.startsWith('ollama|') ? config.getProperty('ai_ollama_service_address') : null // default for openrouter
String systemMessage = 'You are an expert editor specializing in making text clearer, more concise, and more impactful. Your task is to rewrite the text provided as user prompt while maintaining the original meaning and intent, and to change only what is necessary. Output ONLY the rewritten text.'
String iconHourglass = 'emoji-23F3'

def makeDetails = { String t -> "${new Date().format('yyyy-MM-dd HH:mm:ss')} ${model}${t ? '\n' + t : ''}" }

def n = node
def originalText = n.transformedText
n.detailsText = makeDetails()
n.icons.add(iconHourglass)

new AiClient(url: url, key: key, model: model).chatAsync([systemMessage, originalText])
        {
            n.text = it.aiMessage().text()
            n.details = makeDetails("${it.tokenUsage().inputTokenCount()} | ${it.tokenUsage().outputTokenCount()}\n${originalText}")
            n.icons.remove(iconHourglass)
        }
        {
            n.details = makeDetails("ERROR\n${it}")
            n.icons.remove(iconHourglass)
        }
