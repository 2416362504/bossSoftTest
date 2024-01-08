package com.wys.factory;


import java.net.Socket;

public interface SocketFactory {
    Socket createSocket(String host, int port);
}
