/* RemoteClient.groovy - a client for Freeplane remoteControl.groovy server
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
package io.github.macmarrum.freeplane

class RemoteClient {
    public static host = System.env.FREEPLANE_REMOTE_CONTROL_HOST ?: '127.0.0.1'
    public static port = System.env.FREEPLANE_REMOTE_CONTROL_PORT as Integer ?: 48112
    public static encoding = 'UTF-8'

    static void main(String... args) {
        byte[] data
        final argc = args.size()
        if (argc > 0) {
            assert argc == 1, "Got $argc arguments - expected 1"
            data = args[0].getBytes(encoding)
        } else {
            data = System.in.bytes
        }
        println(transfer(data))
    }

    static String transfer(byte[] data) {
        try (def s = new Socket(host, port)) {
            s.withStreams {input, output ->
                output.write(data)
                s.shutdownOutput()
                return new String(input.readAllBytes(), encoding)
            }
        } catch (IOException e) {
            throw new RuntimeException(e)
        }
    }

    static String transfer(String text) {
        return transfer(text.getBytes(encoding))
    }
}
