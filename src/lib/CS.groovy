/*
 * Copyright (C) 2021, 2022, 2025  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
import org.freeplane.api.NodeRO

class CS {
    private static final String ATTRIB_NAME = 'CS.apply'
    public static final String ATTR = ATTRIB_NAME

    static boolean apply(NodeRO node, boolean condition, String style) {
        if (condition) {
            node[ATTRIB_NAME] = style
        } else {
            if (node[ATTRIB_NAME].text == style)
                node[ATTRIB_NAME] = null
        }
        return condition
    }
}
