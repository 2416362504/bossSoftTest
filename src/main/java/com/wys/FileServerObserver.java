package com.wys;

import com.wys.dao.FileServer;
import com.wys.observer.Observer;
import com.wys.thread.FileServerThread;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.*;

/**
 * @author wys
 * @version 1.0.0
 * @class FileServerObserver$
 * @description
 * @aate 2024/1/7$  16:35$
 */
public class FileServerObserver implements Observer {
    public static Vector<String> userIdList = new Vector<>();
    public static Map<String,Socket> socketMap = new ConcurrentHashMap<>();

    private static final int THREAD_POOL_SIZE = 10;
    //创建线程池
    public static final ExecutorService executorService = new ThreadPoolExecutor(
            THREAD_POOL_SIZE,
            THREAD_POOL_SIZE,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());

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
            executorService.submit(new FileServerThread.FileServerInputTask(id,socket));
            executorService.submit(new FileServerThread.FileServerOutputTask(id,socket));
        }
    }

    public static void main(String[] args) throws IOException {
        FileServer fileServer = new FileServer(8888);
        FileServerObserver fileServerObserver = new FileServerObserver();

        fileServer.addObserver(fileServerObserver);

        fileServer.start();
    }
}

