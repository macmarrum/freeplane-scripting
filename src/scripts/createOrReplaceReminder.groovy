/** Creates or replaces a reminder at a preset time and launches Manage Time dialog so that other parameter can be set
 * Copyright (C) 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Create"})

import groovy.transform.Field
import org.freeplane.api.Node
import org.freeplane.core.ui.components.UITools
import org.freeplane.core.util.MenuUtils
import org.freeplane.features.map.NodeModel

import javax.swing.JOptionPane

// default values
@Field final static Integer HOUR = 9
@Field final static Integer MINUTE = 0
@Field final static String PERIOD_UNIT = 'DAY'
@Field final static Integer PERIOD = 1


def node = node as Node

static Date withMyTime(Date dt = null, Integer hour = null, Integer minute = null) {
    hour = hour ?: HOUR
    minute = minute ?: MINUTE
    Calendar cal
    if (dt)
        cal = dt.toCalendar()
    else {
        cal = Calendar.getInstance()
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.add(Calendar.DAY_OF_MONTH, 1)
    }
    cal.set(Calendar.HOUR_OF_DAY, hour)
    cal.set(Calendar.MINUTE, minute)
    return cal.getTime()
}

def r = node.reminder
def dt = r.remindAt
if (dt) {
    def cal = dt.toCalendar()
    def hour = cal.get(Calendar.HOUR_OF_DAY)
    def minute = cal.get(Calendar.MINUTE)
    println("${hour}:${minute}")
    if (hour != HOUR || minute != MINUTE) {
        def message = "A reminder already exists for ${hour}:${sprintf('%02d', minute)}. Replace it with ${HOUR}:${sprintf('%02d', MINUTE)}"
        def title = 'Confirm reminder time update'
        def answer = UITools.showConfirmDialog(node.delegate as NodeModel, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
        println("answer: $answer")
        if (answer == JOptionPane.YES_OPTION) {
            def script = r.script
            r.createOrReplace(withMyTime(dt), r.periodUnit, r.period)
            r.script = script
        }
    }
} else {
    r.createOrReplace(withMyTime(), PERIOD_UNIT, PERIOD)
}
MenuUtils.executeMenuItems(['TimeManagementAction'])
