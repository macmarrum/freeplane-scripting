/*
 * Copyright (C) 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/AI"})
import io.github.macmarrum.freeplane.AiOllamaClient

node = node as org.freeplane.api.Node

String ollamaAddress = 'http://172.16.2.2:11434'
String model = 'phi4-mini:latest'
String system = 'You are an expert editor specializing in making text clearer, more concise, and more impactful. Your task is to rewrite the following text while maintaining the original meaning and intent.'
new AiOllamaClient(ollamaAddress, model, system).generateAsync(
        node.transformedText,
        { response -> node.note = response },
        { e -> node.note = "Error: ${e.message}" }
)
