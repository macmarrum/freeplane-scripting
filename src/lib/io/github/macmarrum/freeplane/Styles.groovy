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
        def als0 = c.selected.mindMap.root.style.styleNode
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
        def mapBackgroundColor = c.selected.mindMap.backgroundColor
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

    /**
     * Creates a control branch "Styles" as a child of the selected node, by importing the JSON.
     * The branch contains clickable nodes (with script1) to execute various save / restore / delete operations.
     */
    static void createControlBranch(Node parent = null, boolean onLeft = true) {
        if (!parent)
            parent = c.selected
        def tempNode = parent.createChild('temp: import Styles from JSON')
        Import.fromJsonStringBase64('''\
            eyJTdHlsZXMiOnsiQGF0dHJpYnV0ZXMiOnsic2NyaXB0MSI6ImltcG9ydCBpby5naXRodW
            IubWFjbWFycnVtLmZyZWVwbGFuZS5FeHBvcnRcblxuZGVmIHMgPSBbZGV0YWlsczogdHJ1
            ZSwgbm90ZTogZmFsc2UsIGF0dHJpYnV0ZXM6IHRydWUsIGxpbms6IHRydWUsIHRyYW5zZm
            9ybWVkOiBmYWxzZSwgc3R5bGU6IGZhbHNlLCBmb3JtYXR0aW5nOiB0cnVlLCBpY29uczog
            ZmFsc2UsIHNraXAxOiBmYWxzZSwgZGVudWxsaWZ5OiB0cnVlLCBwcmV0dHk6IGZhbHNlXV
            xuZGVmIGpzb25TdHIgPSBFeHBvcnQudG9Kc29uU3RyaW5nKG5vZGUsIHMpXG4vL25vZGUu
            bm90ZSA9IGpzb25TdHJcbmRlZiBiYXNlNjQgPSBqc29uU3RyLmdldEJ5dGVzKCdVVEYtOC
            cpLmVuY29kZUJhc2U2NCgpLnRvU3RyaW5nKClcbmRlZiBzYiA9IG5ldyBTdHJpbmdCdWls
            ZGVyKClcbi8vIHdyYXAgYXQgNzAsIGluZGVudCAzIHRhYnMtd29ydGhcbmRlZiBpbmRlbn
            QgPSAnICcgKiAxMlxuYmFzZTY0LmVhY2hXaXRoSW5kZXggeyB0LCBpIC0+XG4gICAgaWYg
            KGkgJSA3MCA9PSAwKVxuICAgICAgICBzYiA8PCAnXFxuJyA8PCBpbmRlbnRcbiAgICBzYi
            A8PCB0XG59XG50ZXh0VXRpbHMuY29weVRvQ2xpcGJvYXJkKHNiLnRvU3RyaW5nKCkpIiwi
            c2NyaXB0MiI6ImltcG9ydCBpby5naXRodWIubWFjbWFycnVtLmZyZWVwbGFuZS5JbXBvcn
            RcblxuZGVmIGpzb25TdHIgPSBub2RlLm5vdGUudGV4dFxuSW1wb3J0LmZyb21Kc29uU3Ry
            aW5nKGpzb25TdHIsIG5vZGUuY3JlYXRlQ2hpbGQoJ2ltcG9ydGVkJykpIiwic2NyaXB0My
            I6ImltcG9ydCBpby5naXRodWIubWFjbWFycnVtLmZyZWVwbGFuZS5JbXBvcnRcblxuZGVm
            IGpzb25TdHIgPSBub2RlLm5vdGUudGV4dC5zdHJpcEluZGVudCgpLnJlcGxhY2VBbGwoJ1
            xcbicsICcnKVxuSW1wb3J0LmZyb21Kc29uU3RyaW5nQmFzZTY0KGpzb25TdHIsIG5vZGUu
            Y3JlYXRlQ2hpbGQoJ2ltcG9ydGVkJykpIn0sIkFMUyI6eyJvbiI6eyJAYXR0cmlidXRlcy
            I6eyJzY3JpcHQxIjoibWVudVV0aWxzLmV4ZWN1dGVNZW51SXRlbXMoWydBdXRvbWF0aWNM
            YXlvdXRDb250cm9sbGVyQWN0aW9uLkFMTCddKSJ9LCJAbGluayI6Im1lbnVpdGVtOl9FeG
            VjdXRlU2NyaXB0Rm9yU2VsZWN0aW9uQWN0aW9uIiwib2ZmIjp7IkBhdHRyaWJ1dGVzIjp7
            InNjcmlwdDEiOiJtZW51VXRpbHMuZXhlY3V0ZU1lbnVJdGVtcyhbJ0F1dG9tYXRpY0xheW
            91dENvbnRyb2xsZXJBY3Rpb24ubnVsbCddKSJ9LCJAbGluayI6Im1lbnVpdGVtOl9FeGVj
            dXRlU2NyaXB0Rm9yU2VsZWN0aW9uQWN0aW9uIn19LCJiYWNrZ3JvdW5kIjp7InNhdmUgYW
            5kIGRlbGV0ZSBiYWNrZ3JvdW5kIGNvbG9yIjp7IkBhdHRyaWJ1dGVzIjp7InNjcmlwdDEi
            OiJpbXBvcnQgaW8uZ2l0aHViLm1hY21hcnJ1bS5mcmVlcGxhbmUuU3R5bGVzXG5cblN0eW
            xlcy5zYXZlQW5kRGVsZXRlQWxzQ29sb3IoJ2JhY2tncm91bmRDb2xvcicpIn0sIkBsaW5r
            IjoibWVudWl0ZW06X0V4ZWN1dGVTY3JpcHRGb3JTZWxlY3Rpb25BY3Rpb24ifSwiZGVsZX
            RlIGJhY2tncm91bmQgY29sb3IiOnsiQGF0dHJpYnV0ZXMiOnsic2NyaXB0MSI6ImltcG9y
            dCBpby5naXRodWIubWFjbWFycnVtLmZyZWVwbGFuZS5TdHlsZXNcblxuU3R5bGVzLmRlbG
            V0ZUFsc0NvbG9yKCdiYWNrZ3JvdW5kQ29sb3InKSJ9LCJAbGluayI6Im1lbnVpdGVtOl9F
            eGVjdXRlU2NyaXB0Rm9yU2VsZWN0aW9uQWN0aW9uIn0sInJlc3RvcmUgYmFja2dyb3VuZC
            Bjb2xvciI6eyJAYXR0cmlidXRlcyI6eyJzY3JpcHQxIjoiaW1wb3J0IGlvLmdpdGh1Yi5t
            YWNtYXJydW0uZnJlZXBsYW5lLlN0eWxlc1xuXG5TdHlsZXMucmVzdG9yZUFsc0NvbG9yKC
            diYWNrZ3JvdW5kQ29sb3InKSJ9LCJAbGluayI6Im1lbnVpdGVtOl9FeGVjdXRlU2NyaXB0
            Rm9yU2VsZWN0aW9uQWN0aW9uIn0sInJlc3RvcmUgc2VlLXRocm91Z2ggYmFja2dyb3VuZC
            Bjb2xvciI6eyJAYXR0cmlidXRlcyI6eyJzY3JpcHQxIjoiaW1wb3J0IGlvLmdpdGh1Yi5t
            YWNtYXJydW0uZnJlZXBsYW5lLlN0eWxlc1xuXG5TdHlsZXMucmVzdG9yZUFsc0NvbG9yKC
            diYWNrZ3JvdW5kQ29sb3JTZWVUaHJvdWdoJykifSwiQGxpbmsiOiJtZW51aXRlbTpfRXhl
            Y3V0ZVNjcmlwdEZvclNlbGVjdGlvbkFjdGlvbiJ9LCJyZXN0b3JlIHNlZS10aHJvdWdoIG
            JhY2tncm91bmQgY29sb3Igd2l0aCBhbHBoYSBmZiI6eyJAYXR0cmlidXRlcyI6eyJzY3Jp
            cHQxIjoiaW1wb3J0IGlvLmdpdGh1Yi5tYWNtYXJydW0uZnJlZXBsYW5lLlN0eWxlc1xuXG
            5TdHlsZXMucmVzdG9yZUFsc0NvbG9yKCdiYWNrZ3JvdW5kQ29sb3JTZWVUaHJvdWdoJywg
            J2ZmJykifSwiQGxpbmsiOiJtZW51aXRlbTpfRXhlY3V0ZVNjcmlwdEZvclNlbGVjdGlvbk
            FjdGlvbiJ9fSwidGV4dCI6eyJAYXR0cmlidXRlcyI6eyJzY3JpcHQxIjoibm9kZS5jaGls
            ZHJlbi5lYWNoIHsgbiAtPlxuICAgIG5bJ3NjcmlwdDEnXSA9IG5bJ3NjcmlwdDEnXS50ZX
            h0LnJlcGxhY2VBbGwoJ2JhY2tncm91bmQnLCAndGV4dCcpXG59XG4ifSwic2F2ZSBhbmQg
            ZGVsZXRlIHRleHQgY29sb3IiOnsiQGF0dHJpYnV0ZXMiOnsic2NyaXB0MSI6ImltcG9ydC
            Bpby5naXRodWIubWFjbWFycnVtLmZyZWVwbGFuZS5TdHlsZXNcblxuU3R5bGVzLnNhdmVB
            bmREZWxldGVBbHNDb2xvcigndGV4dENvbG9yJykifSwiQGxpbmsiOiJtZW51aXRlbTpfRX
            hlY3V0ZVNjcmlwdEZvclNlbGVjdGlvbkFjdGlvbiJ9LCJkZWxldGUgdGV4dCBjb2xvciI6
            eyJAYXR0cmlidXRlcyI6eyJzY3JpcHQxIjoiaW1wb3J0IGlvLmdpdGh1Yi5tYWNtYXJydW
            0uZnJlZXBsYW5lLlN0eWxlc1xuXG5TdHlsZXMuZGVsZXRlQWxzQ29sb3IoJ3RleHRDb2xv
            cicpIn0sIkBsaW5rIjoibWVudWl0ZW06X0V4ZWN1dGVTY3JpcHRGb3JTZWxlY3Rpb25BY3
            Rpb24ifSwicmVzdG9yZSB0ZXh0IGNvbG9yIjp7IkBhdHRyaWJ1dGVzIjp7InNjcmlwdDEi
            OiJpbXBvcnQgaW8uZ2l0aHViLm1hY21hcnJ1bS5mcmVlcGxhbmUuU3R5bGVzXG5cblN0eW
            xlcy5yZXN0b3JlQWxzQ29sb3IoJ3RleHRDb2xvcicpIn0sIkBsaW5rIjoibWVudWl0ZW06
            X0V4ZWN1dGVTY3JpcHRGb3JTZWxlY3Rpb25BY3Rpb24ifSwicmVzdG9yZSBzZWUtdGhyb3
            VnaCB0ZXh0IGNvbG9yIjp7IkBhdHRyaWJ1dGVzIjp7InNjcmlwdDEiOiJpbXBvcnQgaW8u
            Z2l0aHViLm1hY21hcnJ1bS5mcmVlcGxhbmUuU3R5bGVzXG5cblN0eWxlcy5yZXN0b3JlQW
            xzQ29sb3IoJ3RleHRDb2xvclNlZVRocm91Z2gnKSJ9LCJAbGluayI6Im1lbnVpdGVtOl9F
            eGVjdXRlU2NyaXB0Rm9yU2VsZWN0aW9uQWN0aW9uIn0sInJlc3RvcmUgc2VlLXRocm91Z2
            ggdGV4dCBjb2xvciB3aXRoIGFscGhhIGZmIjp7IkBhdHRyaWJ1dGVzIjp7InNjcmlwdDEi
            OiJpbXBvcnQgaW8uZ2l0aHViLm1hY21hcnJ1bS5mcmVlcGxhbmUuU3R5bGVzXG5cblN0eW
            xlcy5yZXN0b3JlQWxzQ29sb3IoJ3RleHRDb2xvclNlZVRocm91Z2gnLCAnZmYnKSJ9LCJA
            bGluayI6Im1lbnVpdGVtOl9FeGVjdXRlU2NyaXB0Rm9yU2VsZWN0aW9uQWN0aW9uIn19LC
            Jib3JkZXIiOnsiQGF0dHJpYnV0ZXMiOnsic2NyaXB0MSI6Im5vZGUuY2hpbGRyZW4uZWFj
            aCB7IG4gLT5cbiAgICBuLnRleHQgPSBuLnRleHQucmVwbGFjZUFsbCgnYmFja2dyb3VuZC
            csICdib3JkZXInKVxuICAgIG5bJ3NjcmlwdDEnXSA9IG5bJ3NjcmlwdDEnXS50ZXh0LnJl
            cGxhY2VBbGwoJ2JhY2tncm91bmRDb2xvcicsICdib3JkZXIuY29sb3InKVxufVxuIn0sIn
            NhdmUgYW5kIGRlbGV0ZSBib3JkZXIgY29sb3IiOnsiQGF0dHJpYnV0ZXMiOnsic2NyaXB0
            MSI6ImltcG9ydCBpby5naXRodWIubWFjbWFycnVtLmZyZWVwbGFuZS5TdHlsZXNcblxuU3
            R5bGVzLnNhdmVBbmREZWxldGVBbHNDb2xvcignYm9yZGVyLmNvbG9yJykifSwiQGxpbmsi
            OiJtZW51aXRlbTpfRXhlY3V0ZVNjcmlwdEZvclNlbGVjdGlvbkFjdGlvbiJ9LCJkZWxldG
            UgYm9yZGVyIGNvbG9yIjp7IkBhdHRyaWJ1dGVzIjp7InNjcmlwdDEiOiJpbXBvcnQgaW8u
            Z2l0aHViLm1hY21hcnJ1bS5mcmVlcGxhbmUuU3R5bGVzXG5cblN0eWxlcy5kZWxldGVBbH
            NDb2xvcignYm9yZGVyLmNvbG9yJykifSwiQGxpbmsiOiJtZW51aXRlbTpfRXhlY3V0ZVNj
            cmlwdEZvclNlbGVjdGlvbkFjdGlvbiJ9LCJyZXN0b3JlIGJvcmRlciBjb2xvciI6eyJAYX
            R0cmlidXRlcyI6eyJzY3JpcHQxIjoiaW1wb3J0IGlvLmdpdGh1Yi5tYWNtYXJydW0uZnJl
            ZXBsYW5lLlN0eWxlc1xuXG5TdHlsZXMucmVzdG9yZUFsc0NvbG9yKCdib3JkZXIuY29sb3
            InKSJ9LCJAbGluayI6Im1lbnVpdGVtOl9FeGVjdXRlU2NyaXB0Rm9yU2VsZWN0aW9uQWN0
            aW9uIn0sInJlc3RvcmUgYm9yZGVyIGNvbG9yIHdpdGggYWxwaGEgMDAiOnsiQGF0dHJpYn
            V0ZXMiOnsic2NyaXB0MSI6ImltcG9ydCBpby5naXRodWIubWFjbWFycnVtLmZyZWVwbGFu
            ZS5TdHlsZXNcblxuU3R5bGVzLnJlc3RvcmVBbHNDb2xvcignYm9yZGVyLmNvbG9yJywgJz
            AwJykifSwiQGxpbmsiOiJtZW51aXRlbTpfRXhlY3V0ZVNjcmlwdEZvclNlbGVjdGlvbkFj
            dGlvbiJ9LCJyZXN0b3JlIHNlZS10aHJvdWdoIGJvcmRlciBjb2xvciI6eyJAYXR0cmlidX
            RlcyI6eyJzY3JpcHQxIjoiaW1wb3J0IGlvLmdpdGh1Yi5tYWNtYXJydW0uZnJlZXBsYW5l
            LlN0eWxlc1xuXG5TdHlsZXMucmVzdG9yZUFsc0NvbG9yKCdib3JkZXIuY29sb3JTZWVUaH
            JvdWdoJykifSwiQGxpbmsiOiJtZW51aXRlbTpfRXhlY3V0ZVNjcmlwdEZvclNlbGVjdGlv
            bkFjdGlvbiJ9LCJyZXN0b3JlIHNlZS10aHJvdWdoIGJvcmRlciBjb2xvciB3aXRoIGFscG
            hhIGZmIjp7IkBhdHRyaWJ1dGVzIjp7InNjcmlwdDEiOiJpbXBvcnQgaW8uZ2l0aHViLm1h
            Y21hcnJ1bS5mcmVlcGxhbmUuU3R5bGVzXG5cblN0eWxlcy5yZXN0b3JlQWxzQ29sb3IoJ2
            JvcmRlci5jb2xvclNlZVRocm91Z2gnLCAnZmYnKSJ9LCJAbGluayI6Im1lbnVpdGVtOl9F
            eGVjdXRlU2NyaXB0Rm9yU2VsZWN0aW9uQWN0aW9uIn0sInNhdmUgYm9yZGVyIGNvbG9yIG
            FzIFNlZVRocm91Z2gsIGZpeCBpdCBhbmQgc2F2ZSBpdCBhcyBib3JkZXIgY29sb3IsIHRo
            ZW4gcmVzdG9yZSBpdCI6eyJAYXR0cmlidXRlcyI6eyJzY3JpcHQxIjoiaW1wb3J0IGlvLm
            dpdGh1Yi5tYWNtYXJydW0uZnJlZXBsYW5lLlN0eWxlc1xuXG5TdHlsZXMuc2F2ZUZpeFJl
            c3RvcmVBbHNDb2xvclNlZVRocm91Z2goJ2JvcmRlci5jb2xvcicpIn0sIkBsaW5rIjoibW
            VudWl0ZW06X0V4ZWN1dGVTY3JpcHRGb3JTZWxlY3Rpb25BY3Rpb24ifX19LCJub2RlIGNv
            bG9ycyI6eyJAZGV0YWlscyI6InRleHRcbmJhY2tncm91bmQiLCJAYXR0cmlidXRlcyI6ey
            JzY3JpcHQxIjoibm9kZS5jaGlsZHJlbi5lYWNoIHsgbiAtPlxuICAgIGRlZiBrID0gbi5h
            cHBlbmRDaGlsZChuKVxuICAgIGsuc3R5bGUuYmFja2dyb3VuZENvbG9yID0gbi5zdHlsZS
            50ZXh0Q29sb3Jcbn0iLCJzY3JpcHQyIjoibm9kZS5jaGlsZHJlbi5lYWNoIHsgbiAtPlxu
            ICAgIGRlZiBrID0gbi5jaGlsZHJlblswXVxuICAgIC8vZGVmIGwgPSBrLmFwcGVuZENoaW
            xkKGspXG4gICAgZGVmIGwgPSBrLmNoaWxkcmVuWzBdXG4gICAgZGVmIGJjYyA9IGsuc3R5
            bGUuYmFja2dyb3VuZENvbG9yQ29kZSArICczMydcbiAgICBrLnN0eWxlLmJhY2tncm91bm
            RDb2xvckNvZGUgPSBiY2NcbiAgICBsLnN0eWxlLmJhY2tncm91bmRDb2xvckNvZGUgPSBi
            Y2Ncbn1cbiIsInNjcmlwdDMiOiJpbXBvcnQgc3RhdGljIG9yZy5mcmVlcGxhbmUuY29yZS
            51dGlsLkNvbG9yVXRpbHMubWFrZU5vblRyYW5zcGFyZW50XG5cbmRlZiBtYmMgPSBub2Rl
            Lm1pbmRNYXAuYmFja2dyb3VuZENvbG9yXG5jLnNlbGVjdGVkcy5lYWNoIHtcbiAgICBpdC
            5zdHlsZS50ZXh0Q29sb3IgPSBtYWtlTm9uVHJhbnNwYXJlbnQoaXQuc3R5bGUudGV4dENv
            bG9yLCBtYmMpXG59Iiwic2NyaXB0NCI6ImRlZiBmID0gJycnPWltcG9ydCBzdGF0aWMgb3
            JnLmZyZWVwbGFuZS5jb3JlLnV0aWwuQ29sb3JVdGlscy5jb2xvclRvUkdCQVN0cmluZ1xu
            Y29sb3JUb1JHQkFTdHJpbmcobm9kZS5zdHlsZS5iYWNrZ3JvdW5kQ29sb3IpJycnXG5cbm
            5vZGUuZmluZEFsbCgpLmVhY2gge1xuICAgIGlmIChpdC5pc0xlYWYoKSkge1xuICAgICAg
            ICBpdC50ZXh0ID0gKGYgKyAnWzEuLi0zXScpXG4gICAgICAgIGl0LnBhcmVudC50ZXh0ID
            0gKGYgKyAnWzEuLi0xXScpXG4gICAgfVxufSJ9LCJJRF8xNTc2NTI1NDgiOnsiQGNvcmUi
            OiI9bm9kZS5zdHlsZS50ZXh0Q29sb3JDb2RlWzEuLi0xXSIsIkBkZXRhaWxzIjoiPWltcG
            9ydCBzdGF0aWMgb3JnLmZyZWVwbGFuZS5jb3JlLnV0aWwuQ29sb3JVdGlscy5jb2xvclRv
            UkdCQVN0cmluZ1xuY29sb3JUb1JHQkFTdHJpbmcobm9kZS5zdHlsZS5iYWNrZ3JvdW5kQ2
            9sb3IpWzEuLi0zXSIsIkBiYWNrZ3JvdW5kQ29sb3IiOiIjMzY2YzQ0ZmYiLCJAdGV4dENv
            bG9yIjoiIzUyZDI3M2ZmIiwiPWltcG9ydCBzdGF0aWMgb3JnLmZyZWVwbGFuZS5jb3JlLn
            V0aWwuQ29sb3JVdGlscy5jb2xvclRvUkdCQVN0cmluZ1xuY29sb3JUb1JHQkFTdHJpbmco
            bm9kZS5zdHlsZS50ZXh0Q29sb3IpWzEuLi0xXSI6eyJAZGV0YWlscyI6Ij1pbXBvcnQgc3
            RhdGljIG9yZy5mcmVlcGxhbmUuY29yZS51dGlsLkNvbG9yVXRpbHMuY29sb3JUb1JHQkFT
            dHJpbmdcbmNvbG9yVG9SR0JBU3RyaW5nKG5vZGUuc3R5bGUuYmFja2dyb3VuZENvbG9yKV
            sxLi4tMV0iLCJAYmFja2dyb3VuZENvbG9yIjoiIzM2NmM0NDMzIiwiQHRleHRDb2xvciI6
            IiM1MmQyNzNiNiIsIj1ub2RlLnN0eWxlLnRleHRDb2xvckNvZGVbMS4uLTFdIjp7IkBkZX
            RhaWxzIjoiPWltcG9ydCBzdGF0aWMgb3JnLmZyZWVwbGFuZS5jb3JlLnV0aWwuQ29sb3JV
            dGlscy5jb2xvclRvUkdCQVN0cmluZ1xuY29sb3JUb1JHQkFTdHJpbmcobm9kZS5zdHlsZS
            5iYWNrZ3JvdW5kQ29sb3IpWzEuLi0zXSIsIkBiYWNrZ3JvdW5kQ29sb3IiOiIjMmQzODMw
            ZmYiLCJAdGV4dENvbG9yIjoiIzQ2YTI1ZWZmIn19fSwiSURfMTIyNTQ3MTE5MCI6eyJAY2
            9yZSI6Ij1ub2RlLnN0eWxlLnRleHRDb2xvckNvZGVbMS4uLTFdIiwiQGRldGFpbHMiOiI9
            aW1wb3J0IHN0YXRpYyBvcmcuZnJlZXBsYW5lLmNvcmUudXRpbC5Db2xvclV0aWxzLmNvbG
            9yVG9SR0JBU3RyaW5nXG5jb2xvclRvUkdCQVN0cmluZyhub2RlLnN0eWxlLmJhY2tncm91
            bmRDb2xvcilbMS4uLTNdIiwiQGJhY2tncm91bmRDb2xvciI6IiM4YjJjMzlmZiIsIkB0ZX
            h0Q29sb3IiOiIjZTk0ZjY0ZmYiLCI9aW1wb3J0IHN0YXRpYyBvcmcuZnJlZXBsYW5lLmNv
            cmUudXRpbC5Db2xvclV0aWxzLmNvbG9yVG9SR0JBU3RyaW5nXG5jb2xvclRvUkdCQVN0cm
            luZyhub2RlLnN0eWxlLnRleHRDb2xvcilbMS4uLTFdIjp7IkBkZXRhaWxzIjoiPWltcG9y
            dCBzdGF0aWMgb3JnLmZyZWVwbGFuZS5jb3JlLnV0aWwuQ29sb3JVdGlscy5jb2xvclRvUk
            dCQVN0cmluZ1xuY29sb3JUb1JHQkFTdHJpbmcobm9kZS5zdHlsZS5iYWNrZ3JvdW5kQ29s
            b3IpWzEuLi0xXSIsIkBiYWNrZ3JvdW5kQ29sb3IiOiIjOGIyYzM5MzMiLCJAdGV4dENvbG
            9yIjoiI2U5NGY2NGI2IiwiPW5vZGUuc3R5bGUudGV4dENvbG9yQ29kZVsxLi4tMV0iOnsi
            QGRldGFpbHMiOiI9bm9kZS5zdHlsZS5iYWNrZ3JvdW5kQ29sb3JDb2RlWzEuLi0xXSIsIk
            BiYWNrZ3JvdW5kQ29sb3IiOiIjM2UyYjJkZmYiLCJAdGV4dENvbG9yIjoiI2IyNDQ1M2Zm
            In19fSwiSURfMTE2NTcwNjUxMyI6eyJAY29yZSI6Ij1ub2RlLnN0eWxlLnRleHRDb2xvck
            NvZGVbMS4uLTFdIiwiQGRldGFpbHMiOiI9aW1wb3J0IHN0YXRpYyBvcmcuZnJlZXBsYW5l
            LmNvcmUudXRpbC5Db2xvclV0aWxzLmNvbG9yVG9SR0JBU3RyaW5nXG5jb2xvclRvUkdCQV
            N0cmluZyhub2RlLnN0eWxlLmJhY2tncm91bmRDb2xvcilbMS4uLTNdIiwiQGJhY2tncm91
            bmRDb2xvciI6IiM4NDcwMmRmZiIsIkB0ZXh0Q29sb3IiOiIjZTVjMzUxZmYiLCI9aW1wb3
            J0IHN0YXRpYyBvcmcuZnJlZXBsYW5lLmNvcmUudXRpbC5Db2xvclV0aWxzLmNvbG9yVG9S
            R0JBU3RyaW5nXG5jb2xvclRvUkdCQVN0cmluZyhub2RlLnN0eWxlLnRleHRDb2xvcilbMS
            4uLTFdIjp7IkBkZXRhaWxzIjoiPWltcG9ydCBzdGF0aWMgb3JnLmZyZWVwbGFuZS5jb3Jl
            LnV0aWwuQ29sb3JVdGlscy5jb2xvclRvUkdCQVN0cmluZ1xuY29sb3JUb1JHQkFTdHJpbm
            cobm9kZS5zdHlsZS5iYWNrZ3JvdW5kQ29sb3IpWzEuLi0xXSIsIkBiYWNrZ3JvdW5kQ29s
            b3IiOiIjODQ3MDJkMzMiLCJAdGV4dENvbG9yIjoiI2U1YzM1MWI2IiwiPW5vZGUuc3R5bG
            UudGV4dENvbG9yQ29kZVsxLi4tMV0iOnsiQGRldGFpbHMiOiI9bm9kZS5zdHlsZS5iYWNr
            Z3JvdW5kQ29sb3JDb2RlWzEuLi0xXSIsIkBiYWNrZ3JvdW5kQ29sb3IiOiIjM2MzODJiZm
            YiLCJAdGV4dENvbG9yIjoiI2FmOTc0NmZmIn19fSwiSURfMTgwNTc1NzUwMyI6eyJAY29y
            ZSI6Ij1ub2RlLnN0eWxlLnRleHRDb2xvckNvZGVbMS4uLTFdIiwiQGRldGFpbHMiOiI9aW
            1wb3J0IHN0YXRpYyBvcmcuZnJlZXBsYW5lLmNvcmUudXRpbC5Db2xvclV0aWxzLmNvbG9y
            VG9SR0JBU3RyaW5nXG5jb2xvclRvUkdCQVN0cmluZyhub2RlLnN0eWxlLmJhY2tncm91bm
            RDb2xvcilbMS4uLTNdIiwiQGJhY2tncm91bmRDb2xvciI6IiMyYzY1NzZmZiIsIkB0ZXh0
            Q29sb3IiOiIjNDViY2RmZmYiLCI9aW1wb3J0IHN0YXRpYyBvcmcuZnJlZXBsYW5lLmNvcm
            UudXRpbC5Db2xvclV0aWxzLmNvbG9yVG9SR0JBU3RyaW5nXG5jb2xvclRvUkdCQVN0cmlu
            Zyhub2RlLnN0eWxlLnRleHRDb2xvcilbMS4uLTFdIjp7IkBkZXRhaWxzIjoiPWltcG9ydC
            BzdGF0aWMgb3JnLmZyZWVwbGFuZS5jb3JlLnV0aWwuQ29sb3JVdGlscy5jb2xvclRvUkdC
            QVN0cmluZ1xuY29sb3JUb1JHQkFTdHJpbmcobm9kZS5zdHlsZS5iYWNrZ3JvdW5kQ29sb3
            IpWzEuLi0xXSIsIkBiYWNrZ3JvdW5kQ29sb3IiOiIjMmM2NTc2MzMiLCJAdGV4dENvbG9y
            IjoiIzQ1YmNkZmI2IiwiPW5vZGUuc3R5bGUudGV4dENvbG9yQ29kZVsxLi4tMV0iOnsiQG
            RldGFpbHMiOiI9bm9kZS5zdHlsZS5iYWNrZ3JvdW5kQ29sb3JDb2RlWzEuLi0xXSIsIkBi
            YWNrZ3JvdW5kQ29sb3IiOiIjMmIzNjNhZmYiLCJAdGV4dENvbG9yIjoiIzNkOTJhYmZmIn
            19fSwiSURfNjY3Mzc1OTg1Ijp7IkBjb3JlIjoiPW5vZGUuc3R5bGUudGV4dENvbG9yQ29k
            ZVsxLi4tMV0iLCJAZGV0YWlscyI6Ij1pbXBvcnQgc3RhdGljIG9yZy5mcmVlcGxhbmUuY2
            9yZS51dGlsLkNvbG9yVXRpbHMuY29sb3JUb1JHQkFTdHJpbmdcbmNvbG9yVG9SR0JBU3Ry
            aW5nKG5vZGUuc3R5bGUuYmFja2dyb3VuZENvbG9yKVsxLi4tM10iLCJAYmFja2dyb3VuZE
            NvbG9yIjoiIzY4MzA1NWZmIiwiQHRleHRDb2xvciI6IiNkMzQ5YTRmZiIsIj1pbXBvcnQg
            c3RhdGljIG9yZy5mcmVlcGxhbmUuY29yZS51dGlsLkNvbG9yVXRpbHMuY29sb3JUb1JHQk
            FTdHJpbmdcbmNvbG9yVG9SR0JBU3RyaW5nKG5vZGUuc3R5bGUudGV4dENvbG9yKVsxLi4t
            MV0iOnsiQGRldGFpbHMiOiI9aW1wb3J0IHN0YXRpYyBvcmcuZnJlZXBsYW5lLmNvcmUudX
            RpbC5Db2xvclV0aWxzLmNvbG9yVG9SR0JBU3RyaW5nXG5jb2xvclRvUkdCQVN0cmluZyhu
            b2RlLnN0eWxlLmJhY2tncm91bmRDb2xvcilbMS4uLTFdIiwiQGJhY2tncm91bmRDb2xvci
            I6IiM2ODMwNTUzMyIsIkB0ZXh0Q29sb3IiOiIjZDM0OWE0YjYiLCI9bm9kZS5zdHlsZS50
            ZXh0Q29sb3JDb2RlWzEuLi0xXSI6eyJAZGV0YWlscyI6Ij1ub2RlLnN0eWxlLmJhY2tncm
            91bmRDb2xvckNvZGVbMS4uLTFdIiwiQGJhY2tncm91bmRDb2xvciI6IiMzNzJjMzNmZiIs
            IkB0ZXh0Q29sb3IiOiIjYTI0MDgxZmYifX19LCJJRF8xODY3NjEyMzQ1Ijp7IkBjb3JlIj
            oiPW5vZGUuc3R5bGUudGV4dENvbG9yQ29kZVsxLi4tMV0iLCJAZGV0YWlscyI6Ij1pbXBv
            cnQgc3RhdGljIG9yZy5mcmVlcGxhbmUuY29yZS51dGlsLkNvbG9yVXRpbHMuY29sb3JUb1
            JHQkFTdHJpbmdcbmNvbG9yVG9SR0JBU3RyaW5nKG5vZGUuc3R5bGUuYmFja2dyb3VuZENv
            bG9yKVsxLi4tM10iLCJAYmFja2dyb3VuZENvbG9yIjoiIzg3NDEyZmZmIiwiQHRleHRDb2
            xvciI6IiNlNTcwNTNmZiIsIj1pbXBvcnQgc3RhdGljIG9yZy5mcmVlcGxhbmUuY29yZS51
            dGlsLkNvbG9yVXRpbHMuY29sb3JUb1JHQkFTdHJpbmdcbmNvbG9yVG9SR0JBU3RyaW5nKG
            5vZGUuc3R5bGUudGV4dENvbG9yKVsxLi4tMV0iOnsiQGRldGFpbHMiOiI9aW1wb3J0IHN0
            YXRpYyBvcmcuZnJlZXBsYW5lLmNvcmUudXRpbC5Db2xvclV0aWxzLmNvbG9yVG9SR0JBU3
            RyaW5nXG5jb2xvclRvUkdCQVN0cmluZyhub2RlLnN0eWxlLmJhY2tncm91bmRDb2xvcilb
            MS4uLTFdIiwiQGJhY2tncm91bmRDb2xvciI6IiM4NzQxMmYzMyIsIkB0ZXh0Q29sb3IiOi
            IjZTU3MDUzYjYiLCI9bm9kZS5zdHlsZS50ZXh0Q29sb3JDb2RlWzEuLi0xXSI6eyJAZGV0
            YWlscyI6Ij1ub2RlLnN0eWxlLmJhY2tncm91bmRDb2xvckNvZGVbMS4uLTFdIiwiQGJhY2
            tncm91bmRDb2xvciI6IiMzZDJmMmJmZiIsIkB0ZXh0Q29sb3IiOiIjYWY1YzQ3ZmYifX19
            fSwiZWRnZSBjb2xvcnMiOnsiQGRldGFpbHMiOiJ3aXAiLCJAYXR0cmlidXRlcyI6eyJzY3
            JpcHQxIjoibm9kZS5jaGlsZHJlbi5lYWNoIHtcbiAgICBpdC5zdHlsZS5iYWNrZ3JvdW5k
            Q29sb3JDb2RlID0gXCIjJGl0LnRleHRcIlxufSJ9LCI0Y2M0NmIiOnsiQGJhY2tncm91bm
            RDb2xvciI6IiM0Y2M0NmJmZiIsIkB0ZXh0Q29sb3IiOiIjMDAwMDAwZmYiLCI0Y2M0NmIi
            OnsiQGJhY2tncm91bmRDb2xvciI6IiMyMjM5MjhmZiIsIkB0ZXh0Q29sb3IiOiIjY2NjY2
            NjZmYifX0sImU5NTA2NSI6eyJAYmFja2dyb3VuZENvbG9yIjoiI2U5NTA2NWZmIiwiQHRl
            eHRDb2xvciI6IiMwMDAwMDBmZiIsImU5NTA2NSI6eyJAYmFja2dyb3VuZENvbG9yIjoiIz
            hiMmMzOWZmIiwiQHRleHRDb2xvciI6IiNjY2NjY2NmZiJ9fSwiZDdiODRkIjp7IkBiYWNr
            Z3JvdW5kQ29sb3IiOiIjZDdiODRkZmYiLCJAdGV4dENvbG9yIjoiIzAwMDAwMGZmIiwiZD
            diODRkIjp7IkBiYWNrZ3JvdW5kQ29sb3IiOiIjNzA2MjMyZmYiLCJAdGV4dENvbG9yIjoi
            I2NjY2NjY2ZmIn19LCI0MWIxZDEiOnsiQGJhY2tncm91bmRDb2xvciI6IiM0MWIxZDFmZi
            IsIkB0ZXh0Q29sb3IiOiIjMDAwMDAwZmYiLCI0MWIxZDEiOnsiQGJhY2tncm91bmRDb2xv
            ciI6IiMyZDU1NjFmZiIsIkB0ZXh0Q29sb3IiOiIjY2NjY2NjZmYifX0sImM1NDQ5OSI6ey
            JAYmFja2dyb3VuZENvbG9yIjoiI2M1NDQ5OWZmIiwiQHRleHRDb2xvciI6IiMwMDAwMDBm
            ZiIsImM1NDQ5OSI6eyJAYmFja2dyb3VuZENvbG9yIjoiIzU0MzA0N2ZmIiwiQHRleHRDb2
            xvciI6IiNjY2NjY2NmZiJ9fSwiZDc2YjRmIjp7IkBiYWNrZ3JvdW5kQ29sb3IiOiIjZDc2
            YjRmZmYiLCJAdGV4dENvbG9yIjoiIzAwMDAwMGZmIiwiZDc2YjRmIjp7IkBiYWNrZ3JvdW
            5kQ29sb3IiOiIjNGMyYjIzZmYiLCJAdGV4dENvbG9yIjoiI2NjY2NjY2ZmIn19fSwianNv
            biI6eyJAYXR0cmlidXRlcyI6eyJzY3JpcHQxIjoiaW1wb3J0IGlvLmdpdGh1Yi5tYWNtYX
            JydW0uZnJlZXBsYW5lLkltcG9ydFxuXG5kZWYganNvblN0ciA9IG1pbmRNYXAucm9vdC5z
            dHlsZS5zdHlsZU5vZGUubm90ZS50ZXh0XG5JbXBvcnQuZnJvbUpzb25TdHJpbmcoanNvbl
            N0ciwgbm9kZSlcbiIsInNjcmlwdDIiOiJpbXBvcnQgaW8uZ2l0aHViLm1hY21hcnJ1bS5m
            cmVlcGxhbmUuRXhwb3J0XG5cbmRlZiBzID0gW2RldGFpbHM6IGZhbHNlLCBub3RlOiBmYW
            xzZSwgYXR0cmlidXRlczogZmFsc2UsIHRyYW5zZm9ybWVkOiBmYWxzZSwgc3R5bGU6IGZh
            bHNlLCBpY29uczogZmFsc2UsIHNraXAxOiB0cnVlLCBkZW51bGxpZnk6IHRydWUsIHByZX
            R0eTogdHJ1ZV1cbmRlZiBqc29uU3RyID0gRXhwb3J0LnRvSnNvblN0cmluZyhub2RlLCBz
            KVxudGV4dFV0aWxzLmNvcHlUb0NsaXBib2FyZChqc29uU3RyKVxubm9kZS5ub3RlID0gan
            NvblN0clxuIn19fX0=
            '''.stripIndent().replaceAll('\n', ''), tempNode)
        def stylesNode = tempNode.children[0]
        stylesNode.moveTo(tempNode.parent)
        tempNode.delete()
        if (onLeft)
            stylesNode.left = onLeft
    }
}
