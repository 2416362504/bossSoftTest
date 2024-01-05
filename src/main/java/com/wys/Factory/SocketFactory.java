package com.wys.Factory;


import java.net.Socket;

public interface SocketFactory {
    Socket createSocket(String host, int port);
}
