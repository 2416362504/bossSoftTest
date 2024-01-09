package com.wys.dao;

import com.wys.observer.Observable;
import com.wys.observer.Observer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.wys.FileServerObserver.executorService;

/**
 * @author wys
 * @version 1.0.0
 * @class FileServer$
 * @description
 * @aate 2023//7$  15:35$
 */
public class FileServer implements Observable {
    private ServerSocket server;
    private List<Observer> observers = new ArrayList<>();

    public FileServer(int port) throws IOException {
        server = new ServerSocket(port);
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message, Socket socket) {
        for (Observer observer : observers) {
            observer.update(message,socket);
        }
    }

    public void start() throws IOException {
        System.out.println("===========File服务端启动================");

        while (true) {
            Socket socket = server.accept();
            System.out.println("有客户端连接");

            InputStream inputStream = socket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            System.out.println("等待客户端发送id消息...");
            String id;
            while ((id = bufferedReader.readLine()) != null) {
                break;
            }
            if("exit".equals(id)){
                System.out.println("服务端退出");
                executorService.shutdown();//关闭线程池
                break;
            }
            System.out.println("客户端的id为：" + id);
            notifyObservers(id, socket);
        }
    }
}
