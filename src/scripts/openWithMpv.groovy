/**
 * Copyright (C) 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
// @ExecutionModes({ON_SINGLE_NODE})


import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.util.LogUtils

import java.util.regex.Matcher

def mpvPath = $/C:\Program Files\mpv\mpv.exe/$
def mpvArgs = ['--force-window=immediate', '--fullscreen=no']

node = node as Node
c = c as Controller

File file
Matcher m
String start = ''
if (file = node.link.file) {
    if (m = node.text =~ /(\b(?:\d\d:)?\d\d:\d\d\b)/) {
        start = "--start=${m[0][1]}"
    }
    def args = [mpvPath, *mpvArgs, file.path, start]
    LogUtils.info(args as String)
    args.execute()
} else {
    c.statusInfo = 'node link not found'
}
