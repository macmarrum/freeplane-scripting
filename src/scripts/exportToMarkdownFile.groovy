/*
 * Copyright (C) 2023, 2024  macmarrum (at) outlook (dot) ie
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
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Export"})


import io.github.macmarrum.freeplane.Export
import org.freeplane.plugin.script.proxy.ScriptUtils

def node = ScriptUtils.node()

def suggestedFile = new File(node.mindMap.file.path.replaceAll(/\.mm$/, '.md'))
def file = Export.askForFile(suggestedFile)
println(":: ${new Date().format('yyyy-MM-dd HH:mm:ss')} exportToMarkdownFile `${file.path}'")
Export.toMarkdownFile(file, node)
