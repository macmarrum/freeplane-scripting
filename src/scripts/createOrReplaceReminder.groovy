/** Creates or replaces a reminder at a preset time and launches Manage Time dialog so that other parameter can be set
 * Copyright (C) 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Create"})

import org.freeplane.api.Node
import org.freeplane.core.util.MenuUtils

def node = node as Node

static Date withMyTime(Date dt = null, int hour = 9, int minute = 0) {
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
    def script = r.script
    r.createOrReplace(withMyTime(dt), r.periodUnit, r.period)
    r.script = script
} else {
    r.createOrReplace(withMyTime(), 'DAY', 1)
}
MenuUtils.executeMenuItems(['TimeManagementAction'])
