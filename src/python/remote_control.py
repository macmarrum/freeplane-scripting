#!/usr/bin/python3
import sys
import socket

host = '127.0.0.1'
port = 48112
encoding = 'UTF-8'


def transfer(text: str) -> str:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((host, port))
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
