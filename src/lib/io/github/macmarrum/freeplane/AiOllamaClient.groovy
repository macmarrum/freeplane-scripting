package io.github.macmarrum.freeplane
/*
 * Copyright (C) 2026  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
import groovy.json.*
import javax.swing.SwingUtilities

class AiOllamaClient {

    String ollamaAddress
    String model
    String system

    AiOllamaClient(
            String ollamaAddress = 'http://127.0.0.1:11434',
            String model = 'phi4-mini:latest',
            String system = 'You are an expert editor specializing in making text clearer, more concise, and more impactful. Your task is to rewrite the following text while maintaining the original meaning and intent.'
    ) {
        this.ollamaAddress = ollamaAddress
        this.model = model
        this.system = system
    }

    String generate(String prompt) {
        def payload = new JsonBuilder([
                model : model,
                system: system,
                prompt: prompt,
                stream: false
        ]).toString()

        def conn = new URL("${ollamaAddress}/api/generate").openConnection() as HttpURLConnection
        conn.with {
            doOutput = true
            requestMethod = 'POST'
            setRequestProperty('Content-Type', 'application/json')
            outputStream.withWriter { it << payload }
            return new JsonSlurper().parse(inputStream).response
        }
    }

    void generateAsync(String prompt, Closure onSuccess, Closure onError = { Exception e -> }) {
        Thread.start {
            try {
                def response = generate(prompt)
                SwingUtilities.invokeLater { onSuccess(response) }
            } catch (Exception e) {
                SwingUtilities.invokeLater { onError(e) }
            }
        }
    }
}
