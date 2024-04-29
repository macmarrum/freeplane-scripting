/*
 * Copyright (C) 2024  macmarrum (at) outlook (dot) ie
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

// https://wjw465150.github.io/blog/Groovy/my_data/Robust_HTML_parsing_the_Groovy_way.htm
// https://stackoverflow.com/questions/68340139/how-to-add-ivy-as-module-dependency-to-intellij

import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.GPathResult

@Grab('org.ccil.cowan.tagsoup:tagsoup:1.2.1')

def sfFilesPage = [
        STABLE : 'https://sourceforge.net/projects/freeplane/files/freeplane%20stable/',
        PREVIEW: 'https://sourceforge.net/projects/freeplane/files/freeplane%20preview/',
]

def artifactType = [
        BIN            : /freeplane_bin/,
        PORTABLE       : /FreeplanePortable-/,
        SETUP_WITH_JAVA: /Freeplane-Setup-With-Java-/,
        SETUP          : /Freeplane-Setup-\d/,
        DMG_APPLE      : /(?<!JDK\d\d)-apple\.dmg/,
        DMG_INTEL      : /(?<!JDK\d\d)-intel\.dmg/,
]

def filesPageUrl = sfFilesPage.PREVIEW
def artifactToDownload = artifactType.BIN
def downloadsDir = '/home/m/Downloads'

def tagsoupParser = new org.ccil.cowan.tagsoup.Parser()
GPathResult html = new XmlSlurper(tagsoupParser).parse(filesPageUrl)
GPathResult file_list = html.depthFirst().find { it.@id = 'files_list' }
GPathResult artifact_tr = file_list.depthFirst().find { it.name() == 'tr' && (it.@title as String) =~ artifactToDownload }
if (artifact_tr === null)
    throw new RuntimeException("artifact $artifactToDownload not found on $filesPageUrl")
String downloadUrl = artifact_tr.th.a.@href
def target = new File(downloadsDir, artifact_tr.@title as String)
if (target.exists()) {
    println "$target already exists"
} else {
    print "downloading to $target ... "
    target.withDataOutputStream { dos ->
        downloadUrl.toURL().eachByte(16 * 1024) { data, count ->
            dos.write(data, 0, count)
        }
    }
    println 'done'
}