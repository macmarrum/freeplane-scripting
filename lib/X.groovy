import groovy.transform.Field
import org.freeplane.api.NodeRO
import org.freeplane.features.format.FormatController
import org.freeplane.plugin.script.proxy.ConvertibleDate
import org.freeplane.plugin.script.proxy.ScriptUtils

import java.text.MessageFormat
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Field final static String df = FormatController.controller.defaultDateFormat.toPattern()
@Field final static String dtf = FormatController.controller.defaultDateTimeFormat.toPattern()
@Field final static String dfShortIsoDay = 'yyyy-MM-dd, E'
@Field final static String dfShortIso = 'yyyy-MM-dd'
@Field final static String dfLongIso = 'yyyy-MM-dd HH:mm'
@Field final static String dfFullIso = '''yyyy-MM-dd'T'HH:mmZ'''
@Field final static String dfLong = 'yyyy-MM-dd,E HH:mm z (Z)'

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
            } catch (DateTimeParseException) {
                // ignore
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
    if (!node)
        node = ScriptUtils.node()
    if (!node[name]) {
        if (!zonedDateTime) {
            // Obtains the current date-time from the system clock in the default time-zone.
            zonedDateTime = ZonedDateTime.now()
        }
        node.style.name = name
        // The pattern must be different than any recognized by Freeplane.
        // If it's recognized, FP'll store the value as LocalDateTime, loosing the original time-zone info
        node[name] = zonedDateTime.format(DateTimeFormatter.ofPattern(dfLong))
    } else {
        ScriptUtils.c().statusInfo = "${name} is already set to ${node[name]}"
    }
}

static String countDescendantsWithStyle(node, String styleName, Boolean canCountAllClones = true, String messageFormatPattern = null) {
    // TODO: count each clone only once
    def cnt = node.findAll().findAll { it.style.name == styleName }.size()
    return MessageFormat.format(messageFormatPattern ?: '{1} count: {0}', cnt, styleName, canCountAllClones)
}