#!/usr/bin/python3
# Copyright (C) 2023-2025  macmarrum (at) outlook (dot) ie
# SPDX-License-Identifier: GPL-3.0-or-later
import os
import socket
import sys
import base64
from datetime import datetime
from pathlib import Path
from textwrap import dedent
from typing import Union

host = os.environ.get('FREEPLANE_REMOTE_CONTROL_HOST', '127.0.0.1')
port = int(port) if (port := os.environ.get('FREEPLANE_REMOTE_CONTROL_PORT')) else 48112
encoding = 'UTF-8'


def transfer(data: Union[bytes, str]) -> str:
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect((host, port))
        s.sendall(data.encode(encoding) if isinstance(data, str) else data)
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


def import_json(json_path: Path, via_base64=False):
    if via_base64:
        json_data = json_path.read_text(encoding=encoding)
        json_base64 = base64.b64encode(json_data.encode(encoding)).decode('L1')
        groovy_script = dedent(f"""\
        import io.github.macmarrum.freeplane.Import
        def n = Import.fromJsonStringBase64('{json_base64}', node)
        c.select(n)
        """)
    else:
        groovy_script = dedent(f"""\
        import io.github.macmarrum.freeplane.Import
        def file = new File({quote_path(json_path)})
        def n = Import.fromJsonFile(file, node)
        c.select(n)
        """)
    return transfer(groovy_script)


if __name__ == '__main__':
    # path to the json file is the first argument to the script
    json_path = Path(sys.argv[1]).absolute()
    result = import_json(json_path)
    print(result)
