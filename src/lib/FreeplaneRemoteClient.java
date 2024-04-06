/* FreeplaneRemoteClient - a client for Freeplane remoteControl.groovy server
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
//package io.github.macmarrum.freeplane;

import java.io.IOException;
import java.net.Socket;

public class FreeplaneRemoteClient {

    private static final String FREEPLANE_REMOTE_CONTROL_ADDRESS = System.getenv("FREEPLANE_REMOTE_CONTROL_ADDRESS");
    public static String address = FREEPLANE_REMOTE_CONTROL_ADDRESS != null ? FREEPLANE_REMOTE_CONTROL_ADDRESS : "127.0.0.1";
    public static final String FREEPLANE_REMOTE_CONTROL_PORT = System.getenv("FREEPLANE_REMOTE_CONTROL_PORT");
    public static Integer port = FREEPLANE_REMOTE_CONTROL_PORT != null ? Integer.getInteger(FREEPLANE_REMOTE_CONTROL_PORT) : 48112;
    public static String encoding = "UTF-8";

    public static void main(String[] args) throws IOException {
        byte[] data;
        final int argc = args.length;
        if (argc > 0) {
            if (argc > 1) {
                throw new AssertionError(String.format("Got %d arguments - expected 1", argc));
            }
            data = args[0].getBytes(encoding);
        } else {
            data = System.in.readAllBytes();
        }
        System.out.println(transfer(data));
    }

    public static String transfer(byte[] data) {
        try (Socket s = new Socket(address, port)) {
            s.getOutputStream().write(data);
            s.shutdownOutput();
            return new String(s.getInputStream().readAllBytes(), encoding);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
