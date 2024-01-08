package com.wys;

import com.wys.dao.FileServer;
import com.wys.observer.Observer;
import com.wys.thread.FileServerThread;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author wys
 * @version 1.0.0
 * @class FileServerObserver$
 * @description
 * @aate 2024/1/7$  16:35$
 */
public class FileServerObserver implements Observer {
    public static List<String> userIdList = new ArrayList<>();
    public static Map<String,Socket> socketMap = new HashMap<>();

    @Override
    public void update(String id, Socket socket) {
        if (userIdList.contains(id)) {
            System.out.println("客户端" + id + "已上线");
            // 处理现有用户逻辑
        } else {
            System.out.println("新客户端" + id + "已上线");
            userIdList.add(id);
            // 处理新用户逻辑
            socketMap.put(id, socket);
            new FileServerThread.FileServerInputThread(id,socket).start();
            new FileServerThread.FileServerOutputThread(id,socket).start();
        }
    }

    public static void main(String[] args) throws IOException {
        FileServer fileServer = new FileServer(8888);
        FileServerObserver fileServerObserver = new FileServerObserver();

        fileServer.addObserver(fileServerObserver);

        fileServer.start();
    }
}

