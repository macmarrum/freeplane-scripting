/*
 * Copyright (C) 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: Apache-2.0
 */

package io.github.macmarrum.swing

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.response.ChatResponse
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.model.openai.OpenAiChatModel
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

import javax.swing.*
import java.time.Duration

/**
 * Example usage:
 * def client = new AiClient(model: 'ollama|phi4-mini')
 * println client.chat(['You are a helpful assistant who answers in few words.', 'How far is the Moon?'])
 */
class AiClient {
    public static String openRouterUrl = 'https://openrouter.ai/api/v1'
    public static String ollamaUrl = 'http://localhost:11434'
    public String url
    public String key
    public String model
    public long timeout

    ChatModel buildModel() {
        if (!model?.contains('|')) {
            throw new IllegalArgumentException("model must be in 'provider|modelName' format, got: '${model}'")
        }
        String provider
        String modelName
        (provider, modelName) = model.split('\\|', 2)
        return switch (provider.toLowerCase()) {
            case 'openrouter' -> {
                def tmout = Duration.ofSeconds(timeout ?: 60)
                OpenAiChatModel.builder().timeout(tmout).baseUrl(url ?: openRouterUrl).apiKey(key).modelName(modelName).build()
            }
            case 'ollama' -> {
                def tmout = Duration.ofSeconds(timeout ?: 300)
                def builder = OllamaChatModel.builder().timeout(tmout).baseUrl(url ?: ollamaUrl).modelName(modelName)
                if (key)
                    builder = builder.customHeaders(['Authorization': "Bearer ${key}"])
                builder.build()
            }
            default -> {
                throw new IllegalArgumentException("provider '${provider}' not supported")
            }
        }
    }

    /**
     * @param messages System Message followed by one or more User Messages
     * @param onSuccess Closure with ChatResponse as its argument
     * @param onError Closure with Exception as its argument
     * @return response text or error
     */
    String chat(List<String> messages, @ClosureParams(value = SimpleType.class, options = 'ChatResponse') Closure onSuccess = null, Closure onError = null) {
        def chatMessages = new ChatMessage[messages.size()]
        messages.eachWithIndex { String msg, int i ->
            chatMessages[i] = i == 0 ? SystemMessage.from(msg) : UserMessage.from(msg)
        }
        return chat(chatMessages, onSuccess, onError)
    }

    String chat(ChatMessage[] chatMessages, @ClosureParams(value = SimpleType.class, options = 'ChatResponse') Closure onSuccess = null, Closure onError = null) {
        def chatModel = buildModel()
        try {
            ChatResponse response = chatModel.chat(chatMessages)
            def text = response.aiMessage().text()
            if (onSuccess != null) {
                SwingUtilities.invokeLater { onSuccess(response) }
            }
            return text
        } catch (Exception e) {
            if (onError != null) {
                SwingUtilities.invokeLater { onError(e) }
                return null
            } else {
                throw e
            }
        }
    }

    void chatAsync(List<String> messages, @ClosureParams(value = SimpleType.class, options = 'ChatResponse') Closure onSuccess, Closure onError) {
        Thread.start { chat(messages, onSuccess, onError) }
    }

    void chatAsync(ChatMessage[] chatMessages, @ClosureParams(value = SimpleType.class, options = 'ChatResponse') Closure onSuccess, Closure onError) {
        Thread.start { chat(chatMessages, onSuccess, onError) }
    }
}
