#!/usr/bin/python3
# Copyright (C) 2023  macmarrum
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
import sys
import base64
from datetime import datetime
from pathlib import Path

from remote_control import transfer

me = Path(__file__)
encoding = 'UTF-8'

# path to the json file is the first argument to the script
json_path = Path(sys.argv[1])
json_data = json_path.read_text(encoding=encoding)
json_base64 = base64.b64encode(json_data.encode(encoding)).decode('L1')
groovy_script = f"""
import io.github.macmarrum.freeplane.Import
def parent = node.mindMap.root.createChild()
parent.text = '{datetime.now().strftime("%Y-%m-%d %H:%M:%S")}'
def n = Import.importJsonBase64('{json_base64}', parent)
c.select(n)
"""
response = transfer(groovy_script)
print(response)
