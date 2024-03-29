#!/usr/bin/python3
# freeplane_remote.py - a client for Freeplane remoteControl.groovy server
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
import os
import sys
import socket

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


if __name__ == '__main__':
    if len(sys.argv) == 2:
        data = sys.argv[1]
    else:
        data = sys.stdin.read()
    print(transfer(data))
