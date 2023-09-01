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

# USAGE
# You can configure this script as a difftool in git, with the name freeplane (as an example):
#  git config --global --add difftool.freeplane.cmd 'path/to/your/freeplane-remote-diff.py "$LOCAL" "$REMOTE"'
# You can omit --global to add it for the current repo only
# You can then use it like this:
#  git difftool -t freeplane HEAD^ path/to/your_mind_map.mm

# REQUIREMENTS
# - remoteControl.groovy → https://github.com/macmarrum/freeplane-scripting/blob/main/src/init/remoteControl.groovy
# - MindMapComparator → https://github.com/macmarrum/freeplane-scripting/blob/main/src/lib/MindMapComparator.groovy
import os
import sys
import socket
from pathlib import Path
from time import sleep

address = os.environ.get('FREEPLANE_REMOTE_CONTROL_ADDRESS', '127.0.0.1')
port = int(port) if (port := os.environ.get('FREEPLANE_REMOTE_CONTROL_PORT')) else 48112
encoding = 'UTF-8'


def transfer(text: str) -> str:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((address, port))
    s.sendall(text.encode(encoding))
    s.shutdown(socket.SHUT_WR)
    response = []
    response_chunk = b'x'
    while response_chunk:
        response_chunk = s.recv(1024)
        response.append(response_chunk.decode(encoding))
    s.close()
    return ''.join(response)


oldMindmap = Path(sys.argv[1]).absolute()
newMindmap = Path(sys.argv[2]).absolute()

groovy_script = f"""
import io.github.macmarrum.freeplane.MindMapComparator
MindMapComparator.compareFiles($/{oldMindmap}/$, $/{newMindmap}/$)
"""
result = transfer(groovy_script)
print(result)
if not result.startswith('ERROR'):
    sleep(30)  # to allow Freeplane to read the files and create a diff mind map before git deletes the temporary files
