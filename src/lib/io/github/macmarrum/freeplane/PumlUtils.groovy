/*
 * Copyright (C) 2025  macmarrum (at) outlook (dot) ie
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
package io.github.macmarrum.freeplane


import org.freeplane.api.Node

import java.util.function.Function

class PumlUtils {
    public static HashMap<String, Object> defaultSettings = [
            skipIconName: 'emoji-274C', // cross mark
            joinIconName: 'emoji-1F300', // cyclone
            taskIconName: 'emoji-1F532', // black square button
    ]

    static String mkUml(Node node, HashMap<String, Object> settings = null) {
        settings = !settings ? defaultSettings.clone() : defaultSettings + settings
        def plantRoot = calcPlantRoot(node)
        def lol = new LinkedList<LinkedList<String>>()
        lol << new LinkedList<String>()
        appendEachRow(plantRoot, lol, settings, this::extractText)
        def code = lol.collect { it.join(' ') }.join('\n')
        return (code =~ /^\n*@startuml\n/ && code =~ /\n@enduml\n*$/) ? code : "@startuml\n$code@enduml" as String
    }

    /** allow PlantUML root node to be either the diagram's first child or the node below the diagram
     */
    static Node calcPlantRoot(Node node) {
        return node.children ? node.children[0] : node.parent.children[node.parent.getChildPosition(node) + 1]
    }

    static String extractText(Node n) {
        return n.transformedText
    }

    static void appendEachRow(Node node, LinkedList<LinkedList<String>> lol, HashMap<String, Object> settings, Function<Node, String> extractContent) {
        if (isJoin(node, settings)) { // join all descendants into a single line
            node.find { !isSkip(it, settings) }.collect { extractContent(it) }.findAll().each { lol[-1] << it }
            lol << new LinkedList<String>()
        } else {
            def nodeChildren = node.children.findAll { !isSkip(it, settings) }
            if (nodeChildren.size() == 1) {
                lol[-1] << extractContent(node)
                appendEachRow(nodeChildren[0], lol, settings, extractContent)
            } else { // no children or many children
                lol[-1] << extractContent(node)
                lol << new LinkedList<String>()
                nodeChildren.each { appendEachRow(it, lol, settings, extractContent) }
            }
        }
    }

    static boolean isJoin(Node n, HashMap<String, Object> settings) {
        return n.icons.contains(settings.joinIconName as String)
    }

    static boolean isSkip(Node n, HashMap<String, Object> settings) {
        def result = n.icons.contains(settings.skipIconName as String)
        println ":: isSkip ${n.text} ${settings.skipIconName as String} => ${result}"
        return result
    }

    static String mkGantt(Node node, HashMap<String, Object> settings = null) {
        settings = !settings ? defaultSettings.clone() : defaultSettings + settings
        def plantRoot = calcPlantRoot(node)
        def lol = new LinkedList<LinkedList<String>>()
        lol << new LinkedList<String>()
        def _extractTextOrIsoDate = { Node n -> extractTextOrIsoDate(n, settings) }
        appendEachRow(plantRoot, lol, settings, _extractTextOrIsoDate)
        // replace `???` with `???'s` if followed by `end` or `start`
        lol.each { listOfRowCells ->
            ['end', 'start'].each { end_or_start ->
                def i = listOfRowCells.indexOf(end_or_start)
                if (i > -1) { // i.e. end_or_start in listOfRowCells
                    listOfRowCells[i - 1] = "${listOfRowCells[i - 1]}'s" as String
                }
            }
        }
        def code = lol.collect { it.join(' ') }.join('\n')
        return (code =~ /^\n*@startgantt\n/ && code =~ /\n@endgantt\n*$/) ? code : "@startgantt\n$code@endgantt" as String
    }

    /** get node's text
     * - transformed text
     * -- additionally surrounded in [] if it's a task
     * - ISO date format if it's a date
     */
    static String extractTextOrIsoDate(Node n, HashMap<String, Object> settings) {
        if (isTask(n, settings)) {
            return "[${n.transformedText}]" as String
        }
        try {
            return n.to.date.format('yyyy-MM-dd')
        } catch (ConversionException) {
            return n.transformedText
        }
    }

    static boolean isTask(Node n, HashMap<String, Object> settings) {
        return n.icons.contains(settings.taskIconName as String)
    }
}
