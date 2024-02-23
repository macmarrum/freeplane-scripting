/*
Copyright (C) 2023, 2024  macmarrum

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

import groovy.json.JsonSlurper
import org.freeplane.api.Node as FN

class NodeIdRefresher {
    public static REFRESHED_PREFIX = 'freshIdToOriginal_'
    public static DATETIME_FORMAT = 'yyyy-MM-dd_HH:mm:ss'
    public static RESTORED_PREFIX = 'restored_'

    static refreshAll(FN node) {
        def mm = node.mindMap
        def root = mm.root
        def originalToFreshId = new LinkedHashMap<String, String>()
        String originalId
        String freshId
        root.findAll().each {
            originalId = it.id
            freshId = originalToFreshId.get(originalId)
            if (freshId === null) { // not yet re-generated
                freshId = mm.delegate.generateNodeID(null) // it makes sure the generated ID is unique in the map
                // guard against using an ID which was used in the original node set, but is now re-generated, so no longer part of the map
                while (originalToFreshId.containsKey(freshId))
                    freshId = mm.delegate.generateNodeID(null)
                originalToFreshId[originalId] = freshId
            }
            it.delegate.setID(freshId)
        }
        persistSavepointData(root, originalToFreshId)
    }

    static persistSavepointData(FN root, HashMap<String, String> originalToFreshId) {
        def sb = new StringBuilder('{\n')
        originalToFreshId.each { k, v -> sb << "\"$v\": \"$k\", \n" }
        sb << '}'

        def now = (new Date()).format(DATETIME_FORMAT)
        root[REFRESHED_PREFIX + now] = sb
    }

    static refresh(FN node) {
        def mm = node.mindMap
        def originalId = node.id
        String freshId = mm.delegate.generateNodeID(null) // it makes sure the generated ID is unique in the map
        node.delegate.setID(freshId)
        persistSavepointData(mm.root, ["$originalId": freshId])
    }

    static Map.Entry<String, Object> getLatestSavepoint(FN root) {
        def savepoints = root.attributes.findAll { Map.Entry<String, Object> it -> it.key.startsWith(REFRESHED_PREFIX) }
        if (savepoints)
            return savepoints.sort().last()
    }

    static restoreLatestSavepoint(FN node) {
        def root = node.mindMap.root
        def savepoint = getLatestSavepoint(root)
        if (savepoint) {
            def hashmapAsJson = savepoint.value as String
            def freshIdToOriginal = new JsonSlurper().parseText(hashmapAsJson)
            String originalId
            root.findAll().each {
                originalId = freshIdToOriginal.get(it.id)
                if (originalId)
                    it.delegate.setID(originalId)
            }
            root[RESTORED_PREFIX + savepoint.key] = savepoint.value
            root.attributes.removeAll(savepoint.key)
        }
    }
}
