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

class PlantUml {
    public static HashMap<String, Object> defaultSettings = new HashMap<>()
    static {
        defaultSettings.skip1 = true // consider the first node a "PlantUml-code parent" and skip it
        defaultSettings.noEntryIconName = 'emoji-26D4' // no entry - ignore the entire branch
        defaultSettings.ignoredIconName = 'emoji-274C' // cross mark - ignore the node
        defaultSettings.sinkIconName = 'emoji-1F300' // cyclone - form one line from the node and all its descendants
        defaultSettings.taskIconName = 'emoji-1F532' // black square button - make it a task by enclosing it in square brackets
    }

    static String makeUml(Node node, HashMap<String, Object> settings = null) {
        settings = (!settings ? defaultSettings.clone() : defaultSettings + settings) as HashMap<String, Object>
        def plantUmlCodeParent = findPlantUmlCodeParent(node, settings)
        def lol = new LinkedList<LinkedList<String>>()
        lol << new LinkedList<String>()
        appendEachRow(plantUmlCodeParent, lol, settings, this::extractText)
        def code = lol.collect { it.join(' ') }.join('\n')
        return (code =~ /^\n*@startuml\n/ && code =~ /\n@enduml\n*$/) ? code : "@startuml\n$code@enduml" as String
    }

    /** Allow PlantUML-code parent to be either the diagram itself or the next sibling, i.e. the node below the diagram
     */
    static Node findPlantUmlCodeParent(Node node, HashMap<String, Object> settings) {
        Node plantUmlCodeParent = null
        def nodeChildren = node.children
        if (nodeChildren && nodeChildren.any { !isNoEntry(it, settings) }) {
            plantUmlCodeParent = node
        } else {
            def nodePosition = node.parent.getChildPosition(node)
            def siblings = node.parent.children
            int pos = 0
            for (Node n in siblings) {
                if (pos > nodePosition && !isNoEntry(n, settings)) {
                    plantUmlCodeParent = n
                    break
                }
                pos++
            }
        }
        return plantUmlCodeParent
    }

    static String extractText(Node n) {
        return n.transformedText
    }

    static void appendEachRow(Node node, LinkedList<LinkedList<String>> lol, HashMap<String, Object> settings, Function<Node, String> extractContent) {
        if (isNoEntry(node, settings))
            return
        boolean skip1 = settings.skip1
        if (isSink(node, settings)) { // join all descendants into a single line
            def nodes = node.findAll()
            if (skip1) {
                nodes.remove(0)
                settings.skip1 = false
            }
            nodes.findAll { !isIgnored(it, settings) }.collect { extractContent(it) }.findAll().each { lol[-1] << it }
            lol << new LinkedList<String>()
        } else {
            def nodeChildren = node.children.findAll { !isNoEntry(it, settings) }
            if (nodeChildren.size() == 1) {
                if (skip1) {
                    settings.skip1 = false
                } else if (!isIgnored(node, settings)) {
                    lol[-1] << extractContent(node)
                }
                appendEachRow(nodeChildren[0], lol, settings, extractContent)
            } else { // no children or many children
                if (skip1) {
                    settings.skip1 = false
                } else {
                    if (!isIgnored(node, settings)) {
                        lol[-1] << extractContent(node)
                        lol << new LinkedList<String>()
                    }
                }
                nodeChildren.each { appendEachRow(it, lol, settings, extractContent) }
            }
        }
    }

    static boolean isNoEntry(Node n, HashMap<String, Object> settings) {
        return n.icons.contains(settings.noEntryIconName as String)
    }

    static boolean isSink(Node n, HashMap<String, Object> settings) {
        return n.icons.contains(settings.sinkIconName as String)
    }

    static boolean isIgnored(Node n, HashMap<String, Object> settings) {
        return n.icons.contains(settings.ignoredIconName as String)
    }

    static String makeGantt(Node node, HashMap<String, Object> settings = null) {
        settings = (!settings ? defaultSettings.clone() : defaultSettings + settings) as HashMap<String, Object>
        def plantUmlCodeParent = findPlantUmlCodeParent(node, settings)
        def lol = new LinkedList<LinkedList<String>>()
        lol << new LinkedList<String>()
        def _extractTextOrIsoDate = { Node n -> extractTextOrIsoDate(n, settings) }
        appendEachRow(plantUmlCodeParent, lol, settings, _extractTextOrIsoDate)
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
        } catch (ignored) {
            return n.transformedText
        }
    }

    static boolean isTask(Node n, HashMap<String, Object> settings) {
        return n.icons.contains(settings.taskIconName as String)
    }
}
