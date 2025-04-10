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

import org.freeplane.api.ConversionException
import org.freeplane.api.Node

class PumlUtils {
    public static HashMap<String, Object> ganttchartSettings = [
            skipIconName: 'emoji-274C', // cross mark
            taskIconName: 'emoji-1F532', // black square button
    ]

    static def mkGanttChart(Node node, HashMap<String, Object> settings = null) {
        settings = !settings ? ganttchartSettings.clone() : ganttchartSettings + settings

        // allow PlantUML root node to be either the digram itself or the node below the diagram
        def plantRoot = node.children ? node : node.parent.children[node.parent.getChildPosition(node) + 1]

        def sb = new StringBuilder()
        plantRoot.children.findAll { !isSkip(it, settings) }.each { firstLevelChild ->
            def firstLevelChildChildren = firstLevelChild.children
            if (firstLevelChildChildren.size() == 1) {
                // not a group header, threfore treat it as a row
                appendRowContent(sb, firstLevelChild, settings)
            } else {
                // a group header, therefore treat each of its children as separate rows
                sb << getTextOrIsoDate(firstLevelChild, settings) << '\n'
                firstLevelChildChildren.findAll { !isSkip(it, settings) }.each { child -> appendRowContent(sb, child, settings) }
            }
        }
        def code = sb.toString()
        return (code =~ /^\n*@startgantt\n/ && code =~ /\n@endgantt\n*$/) ? code : "@startgantt\n$code@endgantt" as String
    }

    static def isSkip(Node n, HashMap<String, Object> settings) {
        n.icons.contains(settings.skipIconName as String)
    }

    // get node's text
    // - transformed text
    // -- additionally surrounded in [] if it's a task
    // - ISO date format if it's a date
    static def getTextOrIsoDate(Node n, HashMap<String, Object> settings) {
        if (isTask(n, settings)) {
            return "[${n.transformedText}]"
        }
        try {
            return n.to.date.format('yyyy-MM-dd')
        } catch (ConversionException) {
            return n.transformedText
        }
    }

    static def isTask(Node n, HashMap<String, Object> settings) {
        n.icons.contains(settings.taskIconName as String)
    }

    static def appendRowContent(StringBuilder sb, Node child, HashMap<String, Object> settings) {
        def listOfGrandchildrenText = child.find { !isSkip(it, settings) }.collect { getTextOrIsoDate(it, settings) }
        // replace `???` with `???'s` if followed by `end` or `start`
        ['end', 'start'].each { end_or_start ->
            def i = listOfGrandchildrenText.indexOf(end_or_start)
            if (i > -1) { // i.e. end_or_start in listOfGrandchildrenText
                listOfGrandchildrenText[i - 1] = "${listOfGrandchildrenText[i - 1]}'s"
            }
        }
        sb << listOfGrandchildrenText.join(' ') << '\n'
    }
}
