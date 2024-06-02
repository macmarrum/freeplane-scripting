/**
 * Copyright (C) 2024  macmarrum (at) outlook (dot) ie
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package io.github.macmarrum.freeplane

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.freeplane.api.Controller
import org.freeplane.api.Node
import org.freeplane.core.util.MenuUtils
import org.freeplane.core.util.TextUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

import java.awt.Color
import java.util.regex.Pattern

import static org.freeplane.core.util.ColorUtils.colorToRGBAString
import static org.freeplane.core.util.ColorUtils.colorToString
import static org.freeplane.core.util.ColorUtils.makeNonTransparent

class Styles {
    private static final Pattern RX_SEE_THROUGH = ~/SeeThrough$/
    private static final Pattern RX_DOUBLE_HEX = ~/\p{XDigit}{2}/
    private static final Controller c = ScriptUtils.c()
    private static final TextUtils textUtils = new TextUtils()

    private static void deb(Object... args) {
        println(":: ${textUtils.defaultDateTimeFormat.format(new Date())} Styles ${args.collect { it as String }.join(' ')}")
    }

    /** Process each ALS and its settings, stored as JSON in ALS::Root's note.
     * Note: The key is a translated name of the level (e.g. "Level 1" in English)
     *
     * @param closure accepting node and hashMap (settings) as its arguments
     */
    static void eachALS(Closure closure) {
        eachALS(closure, false)
    }

    static void eachALS(Closure closure, boolean withRoot) {
        def als0 = ScriptUtils.node().mindMap.root.style.styleNode
        def jsonStr = als0.note?.text
        Map<String, Map<String, Object>> j = (jsonStr ? new JsonSlurper().parseText(jsonStr) : new LinkedHashMap<String, TreeMap<String, Object>>())
        // sort settings-per-level
        j.each { String level, Map<String, Object> h ->
            j[level] = new TreeMap<String, Object>(h)
        }

        def als = als0.parent
        def children = withRoot ? als.children : als.children.drop(1)
        children.each { n ->
            def h = j.get(n.text, new TreeMap<String, Object>())
            closure(n, h)
        }

        als0.note = JsonOutput.prettyPrint(JsonOutput.toJson(j))

        MenuUtils.executeMenuItems([
                'AutomaticLayoutControllerAction.null',
                'AutomaticLayoutControllerAction.ALL'
        ])
    }

    static String deSeeThrough(String name) {
        return name.replaceFirst(RX_SEE_THROUGH, '')
    }

    static void restoreAlsColor(String property, String alpha = null) {
        def methodName = 'restoreAlsColor'
        deb(methodName, "property=$property", "alpha=$alpha")
        assert alpha === null || alpha ==~ RX_DOUBLE_HEX
        def prop = deSeeThrough(property)
        eachALS { Node n, Map<String, Object> h ->
            def colorAsHex = h[property] as String
            if (colorAsHex) {
                if (alpha) {
                    if (colorAsHex.size() == 9)
                        colorAsHex = colorAsHex[0..6]
                    colorAsHex += alpha
                }
                Eval.xy(n, colorAsHex, "x.style.${prop}Code = y")
            } else {
                deb(methodName, "h['$property'] is null!")
            }
        }
    }

    static void saveAndDeleteAlsColor(String property, boolean save = true) {
        def methodName = 'saveAndDeleteAlsColor'
        deb(methodName, "property=$property", "save=$save")
        def prop = deSeeThrough(property)
        eachALS { Node n, Map<String, Object> h ->
            def color = Eval.x(n, "x.style.${prop}") as Color
            if (color) {
                if (save) {
                    // make sure the color to be saved as SeeThrough is actually translucent
                    def isTranslucent = color.alpha > 0 && color.alpha < 255
                    if (property =~ RX_SEE_THROUGH)
                        assert isTranslucent
                    h[property] = isTranslucent ? colorToRGBAString(color) : colorToString(color)
                } else {
                    // make sure the color is already saved
                    assert property in h
                }
                Eval.x(n, "x.style.${prop} = null")
            }
        }
    }

    static void deleteAlsColor(String property) {
        saveAndDeleteAlsColor(property, false)
    }

    /**
     * Saves the color as SeeThrough, fixes it against the map background,
     * saves the fixed color as Color, and then restores it
     *
     * @param property e.g. border.color or textColor
     */
    static void saveFixRestoreAlsColorSeeThrough(String property) {
        def methodName = 'saveFixRestoreAlsColorSeeThrough'
        deb(methodName, "property=$property")
        assert !(property =~ RX_SEE_THROUGH)
        def prop = deSeeThrough(property)
        def mapBackgroundColor = ScriptUtils.node().mindMap.backgroundColor
        Color colorSeeThrough
        Color color
        eachALS { Node n, Map<String, Object> h ->
            colorSeeThrough = Eval.x(n, "x.style.${prop}") as Color
            if (colorSeeThrough && colorSeeThrough.alpha < 255) {
                h."${property}SeeThrough" = colorToRGBAString(colorSeeThrough)
                color = makeNonTransparent(colorSeeThrough, mapBackgroundColor)
                h[property] = colorToString(color)
                Eval.xy(n, color, "x.style.${prop} = y")
            }
        }
    }

    static void restoreAlsFontSize() {
        deb('restoreAlsFontSize')
        eachALS { Node n, Map<String, Object> h ->
            def fontSize = h['font.size'] as Integer
            if (fontSize) {
                n.style.font.size = fontSize
            }
        }
    }
}
