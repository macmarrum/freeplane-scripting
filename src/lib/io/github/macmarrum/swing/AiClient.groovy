/*
 * Copyright (C) 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: Apache-2.0
 */

package io.github.macmarrum.swing

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.response.ChatResponse
import dev.langchain4j.model.googleai.GeminiThinkingConfig
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.model.openai.OpenAiChatModel
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

import javax.swing.*
import java.time.Duration

/**
 * Example usage:
 * def client = new AiClient(provider: 'ollama', model: 'phi4-mini')
 * println client.chat(['You are a helpful assistant who answers in few words.', 'How far is the Moon?'])
 */
class AiClient {
    public static String geminiUrl = 'https://generativelanguage.googleapis.com/v1beta'
    public static String openRouterUrl = 'https://openrouter.ai/api/v1'
    public static String ollamaUrl = 'http://localhost:11434'
    public static Collection<String> thinkEfforts = ['medium', 'high', 'xhigh']
    public String provider
    public String baseUrl
    public String apiKey
    public String model
    public String effort
    public long timeout

    ChatModel buildModel() {
        String modelName
        if (model?.contains('|')) {
            (provider, modelName) = model.split('\\|', 2)
        } else {
            modelName = model
        }
        return switch (provider.toLowerCase()) {
            case 'gemini' -> {
                def tmout = Duration.ofSeconds(timeout ?: 300)
                def thinkingLevel = switch (effort.toLowerCase()) {
                    case 'none' -> null
                    case ['minimal', 'low', 'medium', 'high'] -> effort.toLowerCase()
                    case ['xhigh', 'max'] -> 'high'
                    default -> throw new IllegalArgumentException("unsupported effort: ${effort}")
                }
                def thinkingConfig = GeminiThinkingConfig.builder().thinkingLevel(thinkingLevel).build()
                GoogleAiGeminiChatModel.builder().timeout(tmout).baseUrl(baseUrl ?: geminiUrl).apiKey(apiKey).modelName(modelName).thinkingConfig(thinkingConfig).build()
            }
            case 'openrouter' -> {
                def tmout = Duration.ofSeconds(timeout ?: 60)
                OpenAiChatModel.builder().timeout(tmout).baseUrl(baseUrl ?: openRouterUrl).apiKey(apiKey).modelName(modelName).reasoningEffort(effort).build()
            }
            case 'ollama' -> {
                def tmout = Duration.ofSeconds(timeout ?: 300)
                def think = effort == null ? null : effort in thinkEfforts
                def builder = OllamaChatModel.builder().timeout(tmout).baseUrl(baseUrl ?: ollamaUrl).modelName(modelName).think(think)
                if (apiKey)
                    builder = builder.customHeaders(['Authorization': "Bearer ${apiKey}"])
                builder.build()
            }
            default -> {
                throw new IllegalArgumentException("provider '${provider}' not supported")
            }
        }
    }

    /**
     * @param messages System Message (index: 0) followed by User Messages (index: 1, 3, 5, ...) and AI Messages (index: 2, 4, 6, ...)
     * @param onSuccess Closure with ChatResponse as its argument
     * @param onError Closure with Exception as its argument
     * @return response text or null on error
     */
    String chat(Iterable<String> messages,
                @ClosureParams(value = SimpleType.class, options = 'ChatResponse') Closure onSuccess = null,
                @ClosureParams(value = SimpleType.class, options = 'Exception') Closure onError = null) {
        def chatMessages = new ChatMessage[messages.size()]
        messages.eachWithIndex { String msg, int i ->
            def messageClass = i == 0 ? SystemMessage : (i % 2 != 0 ? UserMessage : AiMessage)
            chatMessages[i] = messageClass.from(msg)
        }
        return chat(chatMessages, onSuccess, onError)
    }

    String chat(ChatMessage[] chatMessages,
                @ClosureParams(value = SimpleType.class, options = 'ChatResponse') Closure onSuccess = null,
                @ClosureParams(value = SimpleType.class, options = 'Exception') Closure onError = null) {
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

    void chatAsync(Iterable<String> messages,
                   @ClosureParams(value = SimpleType.class, options = 'ChatResponse') Closure onSuccess,
                   @ClosureParams(value = SimpleType.class, options = 'Exception') Closure onError) {
        Thread.start { chat(messages, onSuccess, onError) }
    }

    void chatAsync(ChatMessage[] chatMessages,
                   @ClosureParams(value = SimpleType.class, options = 'ChatResponse') Closure onSuccess,
                   @ClosureParams(value = SimpleType.class, options = 'Exception') Closure onError) {
        Thread.start { chat(chatMessages, onSuccess, onError) }
    }
}
