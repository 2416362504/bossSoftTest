package com.wys.observer;

import java.net.Socket;

public interface Observer {
    void update(String message, Socket socket);
}

