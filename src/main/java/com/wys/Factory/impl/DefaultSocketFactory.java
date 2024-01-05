package com.wys.Factory.impl;

import com.wys.Factory.SocketFactory;

import java.io.IOException;
import java.net.Socket;

public class DefaultSocketFactory implements SocketFactory {
    public Socket createSocket(String host, int port) {
        try {
            return new Socket(host, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
