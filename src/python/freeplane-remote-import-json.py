#!/usr/bin/python3
# Copyright (C) 2023, 2024  macmarrum
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
import os
import socket
import sys
import base64
from datetime import datetime
from pathlib import Path
from textwrap import dedent
from typing import Union

via_base64 = False

address = os.environ.get('FREEPLANE_REMOTE_CONTROL_ADDRESS', '127.0.0.1')
port = int(port) if (port := os.environ.get('FREEPLANE_REMOTE_CONTROL_PORT')) else 48112
encoding = 'UTF-8'


def transfer(data: bytes) -> str:
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect((address, port))
        s.sendall(data)
        s.shutdown(socket.SHUT_WR)
        response: list[str] = []
        while response_chunk := s.recv(1024):
            response.append(response_chunk.decode(encoding))
        return ''.join(response)


def quote_path(path: Union[str, Path]) -> str:
    path = str(path)
    if '/' not in path:
        return '/' + path + '/'
    if '$/' not in path and '/$' not in path:
        return '$/' + path + '/$'
    for qt in ["'", '"', "'''", '"""']:
        if qt not in path:
            return qt + path.replace('\\', '\\\\') + qt
    raise ValueError(f"unable to quote path `{path}`")


# path to the json file is the first argument to the script
json_path = Path(sys.argv[1]).absolute()

if via_base64:
    json_data = json_path.read_text(encoding=encoding)
    json_base64 = base64.b64encode(json_data.encode(encoding)).decode('L1')
    groovy_script = dedent(f"""\
    import io.github.macmarrum.freeplane.Import
    def parent = node.mindMap.root.createChild()
    parent.text = '{datetime.now().strftime("%Y-%m-%d %H:%M:%S")}'
    def n = Import.fromJsonStringBase64('{json_base64}', parent)
    c.select(n)
    """)
else:
    groovy_script = dedent(f"""\
    import io.github.macmarrum.freeplane.Import
    def parent = node.mindMap.root.createChild()
    parent.text = '{datetime.now().strftime("%Y-%m-%d %H:%M:%S")}'
    def file = new File({quote_path(json_path)})
    def n = Import.fromJsonFile(file, parent)
    c.select(n)
    """)
response = transfer(groovy_script.encode(encoding))
print(response)
