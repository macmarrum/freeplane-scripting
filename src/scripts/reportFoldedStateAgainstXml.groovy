/**
 * Copyright (C) 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac3"})


import groovy.json.JsonOutput
import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.NodeChild
import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.util.TextUtils

def c = c as Controller

def mm = c.selected.mindMap
def map = new XmlSlurper().parse(mm.file)
def idToFolded = new LinkedHashMap<String, List<Boolean>>()
map.'**'.findAll { it.name() == 'node' }.each { NodeChild it ->
    idToFolded[it.@ID as String] = [it.attributes().getOrDefault('FOLDED', 'false') == 'true']
}
TextUtils.copyToClipboard(JsonOutput.prettyPrint(JsonOutput.toJson(idToFolded)))
c.findAll().each { Node n ->
    def folded = n.isFolded()
    if (folded != idToFolded[n.id][0]) {
        idToFolded[n.id] << folded
        n.conditionalStyles.insert(0, true, null, 'folded.diff', false)
    }
}
