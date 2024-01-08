package com.wys.observer;

import java.net.Socket;

public interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(String message, Socket socket);
}

