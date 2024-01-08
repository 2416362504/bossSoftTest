package com.wys.thread;

import com.wys.FileServerObserver;
import com.wys.utils.FileUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * @Author wys
 * @Date 2024/1/5
 * @Description File传输服务端
 */
public class FileServerThread {
//
//    private static Map<String,Socket> socketMap = new HashMap<>();
//    private static List<String> userIdList = new ArrayList<>();
//
//    public static void main(String[] args) throws IOException {
//        //定义一个ServerSocket服务端对象，并为其绑定端口号
//        ServerSocket server = new ServerSocket(8888);
//        System.out.println("===========File服务端启动================");
//        //对File来讲，每个Socket都需要一个Thread
//        while (true) {
//            //监听客户端Socket连接
//            Socket socket = server.accept();
//            System.out.println("有客户端连接");
//            //从socket中获取输入流
//            InputStream inputStream = socket.getInputStream();
//            //转换为
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//            System.out.println("等待客户端发送id消息...");
//            String id;
//            while ((id = bufferedReader.readLine()) != null) {
//                System.out.println("客户端的id为："+id);
//                break;
//            }
//            if (userIdList.contains(id)) {
//                System.out.println("客户端"+id+"已上线");
//                socket.close();
//            }else {
//                userIdList.add(id);
//                socketMap.put(id, socket);
//                userIdList.forEach(System.out::println);
//                new FileServerInputThread(id,socket).start();
//                new FileServerOutputThread(id,socket).start();
//            }
//        }
//    }

    /**
     * @description: 服务端输入线程
     * @author: wys
     * @date: 2024/01/08  20/07
     */
    public static class FileServerInputThread extends Thread{
        //socket连接
        private Socket socket;
        private String id;

        public FileServerInputThread(String id,Socket socket){
            this.id=id;
            this.socket=socket;
        }

        @Override
        public void run() {
            try {
                //从socket中获取输入流
                InputStream inputStream = socket.getInputStream();
                //转换为
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                while (true) {
                    try {
                        // 读取数据
                        List<String> message = new ArrayList<>();
                        //从Buffer中读取信息，如果读取到信息则输出
                        String msg;
                        while ((msg = bufferedReader.readLine()) != null) {
                            //System.out.println(msg);
                            if ("END_OF_MESSAGE".equals(msg)) {
                                break;
                            }
                            message.add(msg);
                        }
                        //System.out.println("收到的数组为："+message.size());
                        //判断是否为结束信息,如果为空，则关闭连接
                        if (message.size()==0) {
                            System.out.println("客户端下线了"+socket.getRemoteSocketAddress());
                            bufferedReader.close();
                            socket.close();
                            break;
                        }
                        //判断是否为单条信息 如果是则存储到服务端的本地磁盘
                        if(message.size()==1) {
                            System.out.println("收到客户端消息!");
                            msg = FileUtil.decode(message.get(0));
                            System.out.println(msg);
                            if (FileUtil.isXML(msg)) {
                                try (FileWriter writer = new FileWriter("E:\\IDEAWorkspace\\bossSoftTest\\src\\main\\resources\\2.xml")) { // 省略第二个参数的话，写入位置从文件开头开始
                                    writer.write(msg);
                                }
                            } else if (FileUtil.isJSON(msg)) {
                                try (FileWriter writer = new FileWriter("E:\\IDEAWorkspace\\bossSoftTest\\src\\main\\resources\\2.json")) { // 省略第二个参数的话，写入位置从文件开头开始
                                    writer.write(msg);
                                }
                            } else {
                                System.out.println("数据不是json格式或者xml格式，请重新选择：\n");
                            }
                        }
                        //如果是双条信息，则将文件内容发送给指定的客户端
                        if(message.size()==2){
                            System.out.println("要发送的客户端id为："+message.get(0));
                            //message.get(0)为客户端的id，message.get(1)为文件内容
                            new FileOutputToClientThread(message.get(0),message.get(1)).start();
                        }
                    } catch (Exception e) {
                        if (FileServerObserver.userIdList.contains(id)) {
                            FileServerObserver.userIdList.remove(id);
                            FileServerObserver.socketMap.remove(id);
                        }
                        System.out.println("客户端下线了"+socket.getRemoteSocketAddress());
                        bufferedReader.close();
                        socket.close();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @description: 服务端输出线程
     * @author: wys
     * @date: 2024/01/08  20/07
     */
    public static class FileServerOutputThread extends Thread {
        //socket连接
        private Socket socket;
        private String id;
        public FileServerOutputThread(String id, Socket socket){
            this.id=id;
            this.socket=socket;
        }
        @Override
        public void run() {
            try {
                //从socket中获取输出流
                OutputStream outputStream = socket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    try {
                        // 使用非阻塞方式检查是否有输入
                        while (System.in.available() > 0 && scanner.hasNextLine()) {
                            String msg = scanner.nextLine();
                            String[] temp = msg.split(" ");
                            //通过输出流对象向客户端传递信息
                            //outputStream.write(temp[1].getBytes());
                            printStream.println(temp[1]); //有换行的println
                            //System.out.println(temp[1]);
                            outputStream.flush();
                            printStream.println("END_OF_MESSAGE"); //有换行的println
                            outputStream.flush();
                            System.out.println("服务端发送消息成功");
                        }
                        if(socket.isClosed()){
                            if (FileServerObserver.userIdList.contains(id)) {
                                FileServerObserver.userIdList.remove(id);
                                FileServerObserver.socketMap.remove(id);
                            }
                            System.out.println("客户端下线了"+socket.getRemoteSocketAddress());
                            break;
                        }
                    } catch (Exception e) {
                        if (FileServerObserver.userIdList.contains(id)) {
                            FileServerObserver.userIdList.remove(id);
                            FileServerObserver.socketMap.remove(id);
                        }
                        System.out.println("客户端下线了"+socket.getRemoteSocketAddress());
                        printStream.close();
                        socket.close();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @description: 服务端转发线程
     * @author: wys
     * @date: 2024/01/08  20/07
     */
    static class FileOutputToClientThread extends Thread {
        private String id;
        private String msg;

        FileOutputToClientThread(String id,String msg){
            this.id=id;
            this.msg=msg;
        }
        @Override
        public void run() {
            while(true){
                if(!FileServerObserver.userIdList.contains(id)){
                    continue;
                }
                Socket socket = FileServerObserver.socketMap.get(id);
                try {
                    System.out.println("收到客户端发给客户端"+id+"的加密消息：\n" + msg);
                    OutputStream outputStream = socket.getOutputStream();
                    PrintStream printStream = new PrintStream(outputStream);
                    printStream.write(id.getBytes());
                    printStream.write("\n".getBytes());
                    printStream.flush();
                    printStream.write(msg.getBytes());
                    printStream.write("\n".getBytes());
                    printStream.flush();
                    printStream.write("END_OF_MESSAGE".getBytes());
                    printStream.write("\n".getBytes());
                    printStream.flush();
                    System.out.println("成功转发客户端的消息给客户端"+id);
                    break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }
}