/*
 * Copyright (C) 2022-2024  macmarrum
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
// @ExecutionModes({ON_SINGLE_NODE="/menu_bar/Mac1/Copy"})

import org.freeplane.core.util.TextUtils
import org.freeplane.plugin.script.proxy.ScriptUtils

def text = ScriptUtils.c().selecteds.collect { it.transformedText }.join('\n')
TextUtils.copyToClipboard(text)
