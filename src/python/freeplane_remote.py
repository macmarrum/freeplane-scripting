#!/usr/bin/python3
# freeplane_remote.py - a client for Freeplane remoteControl.groovy server
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
import sys
import socket

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


if __name__ == '__main__':
    argc = len(sys.argv)
    if argc > 1:
        assert argc == 2, f"Got ${argc - 1} arguments - expected 1"
        data = sys.argv[1].encode(encoding)
    else:
        data = sys.stdin.read()
    print(transfer(data))
