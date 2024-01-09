package com.wys.observer;

import java.net.Socket;

public interface Observable {
    /*/**
     * @description: 添加观察者
     * @author: wys
     * @date: 2024/01/09  14/37
     * @param: observer
     * @return: void
     */
    void addObserver(Observer observer);
    /*/**
     * @description: 移除观察者
     * @author: wys
     * @date: 2024/01/09  14/37
     * @param: observer
     * @return: void
     */
    void removeObserver(Observer observer);
    /*/**
     * @description: 通知观察者
     * @author: wys
     * @date: 2024/01/09  14/40
     * @param: message 上线用户的id
     *          socket 上线用户的socket
     * @return: void
     */
    void notifyObservers(String message, Socket socket);
}

