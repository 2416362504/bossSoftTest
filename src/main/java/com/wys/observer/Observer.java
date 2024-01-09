package com.wys.observer;

import java.net.Socket;

public interface Observer {
    /*/**
     * @description: 更新客户端上线的消息，判断该客户端是否已经上线。
     * @author: wys
     * @date: 2024/01/09  14/34
     * @param: message 上线用户的id
     *         socket   上线用户的socket
     * @return: void
     */
    void update(String message, Socket socket);
}

