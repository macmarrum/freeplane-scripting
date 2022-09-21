import org.freeplane.api.NodeRO
import org.freeplane.features.format.FormatController
import org.freeplane.plugin.script.proxy.ConvertibleDate
import org.freeplane.plugin.script.proxy.ScriptUtils

import java.text.MessageFormat
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class X {
    final static String df = FormatController.controller.defaultDateFormat.toPattern()
    final static String dtf = FormatController.controller.defaultDateTimeFormat.toPattern()
    final static String dfShortIsoDay = 'yyyy-MM-dd, E'
    final static String dfShortIso = 'yyyy-MM-dd'
    final static String dfLongIso = 'yyyy-MM-dd HH:mm'
    final static String dfFullIso = '''yyyy-MM-dd'T'HH:mmZ'''
    final static String dfLong = 'yyyy-MM-dd,E HH:mm:ss z (Z)'

    static Calendar addDaysCal(NodeRO node, num) {
        def d = node.to.calendar
        d.add(Calendar.DATE, (int) num)
        return d
    }

    static String addDays(NodeRO node, num, dateFormat = df) {
        return addDaysCal(node, num).format(dateFormat)
    }

    static LocalDate localDateParse(String text, String dateFormat = null) {
        if (dateFormat.is(null)) {
            for (format in [df, dfShortIso, dfLongIso, dfFullIso, dfShortIsoDay]) {
                try {
                    return LocalDate.parse(text, format)
                } catch (DateTimeParseException ignore) {
                }
            }
            return LocalDate.parse(text, df)  // will throw an exception describing format mismatch
        } else {
            return LocalDate.parse(text, dateFormat)
        }
    }

    static LocalDate localDateParse(NodeRO node, String dateFormat = null) {
        return localDateParse(node.transformedText, dateFormat)
    }

    static LocalDate localDate(NodeRO node, String dateFormat = null) {
        // node cam be a DateNode with transformedText in one of many date formats
        // therefore, first check it can be converted to date, rather than parsed
        if (node.to.class.is(ConvertibleDate)) {
            return new java.sql.Date(node.to.date.getTime()).toLocalDate()
        } else {
            return localDateParse(node, dateFormat)
        }
    }

    static void setStyleAndTimestampInAttribute(String name, NodeRO node = null, ZonedDateTime zonedDateTime = null) {
        node ?= ScriptUtils.node()
        if (!node[name]) {
            if (!zonedDateTime) {
                // Obtains the current date-time from the system clock in the default time-zone.
                zonedDateTime = ZonedDateTime.now()
            }
            node.style.name = name
            // To store date/time as String, the pattern must be different than any recognized by Freeplane.
            // If it's recognized, FP'll store the value as LocalDateTime, loosing the original time-zone info
            node[name] = zonedDateTime.format(DateTimeFormatter.ofPattern(dfLong))
        } else {
            ScriptUtils.c().statusInfo = "${name} is already set to ${node[name]}"
        }
    }

    static String countDescendantsWithStyle(NodeRO node, String styleName, Boolean isCountAllClones = true, String messageFormatPattern = null) {
        Set<NodeRO> uniqueNodeIDs = new HashSet<>()
        Boolean isCloneExist
        Boolean isCountMeIn
        def cnt = node.findAll().findAll { NodeRO it ->
            if (isCountAllClones) {
                isCountMeIn = true
                if (!isCloneExist)
                    isCloneExist = it.countNodesSharingContent > 0
            } else {  // don't count all clones
                // check if self has already been counted
                if (uniqueNodeIDs.contains(it)) {
                    isCloneExist = true
                    isCountMeIn = false
                } else {
                    isCountMeIn = true
                    uniqueNodeIDs.add(it)
                }
                // add all clones of self
                uniqueNodeIDs.addAll(it.nodesSharingContent)
            }
            isCountMeIn && it.style.name == styleName
        }.size()
        return MessageFormat.format(messageFormatPattern ?: '{1}: {0} {2}', cnt, styleName, isCountAllClones && isCloneExist ? 'nodes, when counting clones' : 'unique nodes')
    }

    static Map<String, Integer> getCountOfDescendantsWithStyle(NodeRO node, String styleName, Boolean isCountAllClones = true) {
        Set<NodeRO> uniqueNodes = new HashSet<>()
        Boolean isCloneExist
        Boolean isCountMeIn
        def cnt = node.findAll().findAll { NodeRO it ->
            if (isCountAllClones) {
                isCountMeIn = true
                if (!isCloneExist)
                    isCloneExist = it.countNodesSharingContent > 0
            } else {  // don't count all clones
                // check if self has already been counted
                if (uniqueNodes.contains(it)) {
                    isCloneExist = true
                    isCountMeIn = false
                } else {
                    isCountMeIn = true
                    uniqueNodes.add(it)
                }
                // add all clones of self
                uniqueNodes.addAll(it.nodesSharingContent)
            }
            isCountMeIn && it.style.name == styleName
        }.size()
        return [count: cnt, hasClones: isCloneExist ? 1 : 0]
    }

    def static reportCountOfDescendantsWithStyle(node = null, styleName = '!WaitingFor') {
        node ?= ScriptUtils.node()
        def uniqueResult = getCountOfDescendantsWithStyle(node, styleName, false)
        def allResult = getCountOfDescendantsWithStyle(node, styleName, true)
        int countAll = allResult.count
        int countUnique = uniqueResult.count
        if (countAll == countUnique)
            return "$styleName: $countAll nodes in total"
        else
            return "$styleName: $countUnique unique nodes, $countAll nodes in total"
    }

    def static getDescendantsWithStyle(NodeRO node = null, String styleName = '!WaitingFor', Boolean isConsiderClones = false) {
        node ?= ScriptUtils.node()
        Set<NodeRO> uniqueNodes = new HashSet<>()
        Boolean isCountMeIn
        return node.findAll().findAll {
            if (it.style.name == styleName) {
                if (isConsiderClones)
                    isCountMeIn = true
                else {
                    // check if self has already been counted
                    if (uniqueNodes.contains(it)) {
                        // a clone exists
                        isCountMeIn = false
                    } else {
                        isCountMeIn = true
                        uniqueNodes.add(it)
                    }
                    // add all clones of self
                    uniqueNodes.addAll(it.nodesSharingContent)
                }
                isCountMeIn
            } else {
                false
            }
        }
    }

    def static getTransformedTextWithNewlinesReplaced(NodeRO n, CharSequence replacement = '||') {
        return n.transformedText.replaceAll(/\n/, replacement)
    }

    static String makeTsv(CharSequence... args) {
        return args.join(/\t/)
    }

    def static tsvDescendantsWithStyle(NodeRO node = null, String styleName = '!WaitingFor', Boolean isConsiderClones = false) {
        def ttf = this.&getTransformedTextWithNewlinesReplaced
        return getDescendantsWithStyle(node, styleName, isConsiderClones).collect {
            "${it.id}\t${it.transformedText.replaceAll(/\n/, '||')}"
        }.join('\n')
    }

    static Boolean makeJson_isMap(NodeRO node) {
        return !makeJson_isList(node) && node.children.any { it.children.size() > 0 }
    }

    static Boolean makeJson_isNum(NodeRO node) {
        return node.icons.icons.contains('emoji-1F522')
    }

    static Boolean makeJson_isList(NodeRO node) {
        return node.icons.icons.contains('list')
    }

    static Boolean makeJson_isListNum(NodeRO node) {
        return makeJson_isList(node) && makeJson_isNum(node)
    }

    static Boolean makeJson_isIgnored(NodeRO node) {
        return node.icons.icons.contains('emoji-26D4')
    }

    static String makeJson(NodeRO node, int level = 1) {
        /* key is always a string
         * value can be
         *  - a map
         *  - a list of nums
         *  - a list of strings/dates
         *  - a num
         *  - a string/date
         */
        final String indentSpaces = '  '
        final String indent = "${indentSpaces * level}"
        String valueJson
        def body = node.children.findAll { !makeJson_isIgnored(it) }.collect { NodeRO key ->
            if (key.children.findAll { !makeJson_isIgnored(it) }.size() == 0)
                return null
            if (makeJson_isMap(key))
                valueJson = makeJson(key, (level + 1))
            else {
                if (makeJson_isList(key)) {
                    if (makeJson_isNum(key))
                        valueJson = "[${key.children.findAll { !makeJson_isIgnored(it) }.collect { it.transformedText }.join(', ')}]"
                    else
                        valueJson = "[${key.children.findAll { !makeJson_isIgnored(it) }.collect { "\"${it.transformedText}\"" }.join(', ')}]"
                } else if (makeJson_isNum(key))
                    valueJson = "${key.children.find { !makeJson_isIgnored(it) }.transformedText}"
                else
                    valueJson = "\"${key.children.find { !makeJson_isIgnored(it) }.transformedText}\""
            }
            "\"${key.transformedText}\": ${valueJson}"
        }.findAll { it }.join(",\n${indent}")
        return "{\n${indent}${body}\n${indentSpaces * (level - 1)}}"
    }


    static String zipJson(NodeRO node) {
        if (node.children.size() != 2) return '----\n-- Assert "2 branches" failed\n----\n'
        // simplified -- no map
        // field name from branchNum 1
        // value (can be a list) from branchNum 2
        final int level = 1
        final String indentSpaces = '  '
        final String indent = "${indentSpaces * level}"
        int smallerBranchChildrenSize = node.children.collect { branch -> branch.children.size() }.min()
        def keys = new HashMap<Integer, NodeRO>()
        def values = []
        NodeRO key
        node.children.eachWithIndex { branch, branchNum ->
            branch.children.eachWithIndex { levelOneChild, int idx ->
                if (idx < smallerBranchChildrenSize) {
                    if (branchNum == 0) {
                        keys[idx] = levelOneChild.children[0]
                    } else {  // branchNum == 1
                        if (levelOneChild.children.findAll { !makeJson_isIgnored(it) }.size() == 0)
                            values << 'null'
                        else {
                            key = keys[idx]
                            if (makeJson_isList(key)) {
                                if (makeJson_isNum(key))
                                    values << /[${levelOneChild.children.findAll { !makeJson_isIgnored(it) }.collect { it.transformedText }.join(', ')}]/
                                else
                                    values << /[${levelOneChild.children.findAll { !makeJson_isIgnored(it) }.collect { "${it.transformedText}" }.join(', ')}]/
                            } else if (makeJson_isNum(key))
                                values << /${levelOneChild.children.find { !makeJson_isIgnored(it) }.transformedText}/
                            else
                                values << /"${levelOneChild.children.find { !makeJson_isIgnored(it) }.transformedText}"/
                        }
                    }
                }
            }
        }
        def jsonList = []
        def valueJson
        keys.eachWithIndex { Map.Entry<Integer, NodeRO> entry, int idx ->
            key = entry.value
            valueJson = values[idx]
            jsonList << "\"${key.transformedText}\": ${valueJson}"
        }
        def body = jsonList.join(",\n${indent}")
        return "{\n${indent}${body}\n${indentSpaces * (level - 1)}}"
    }
}
