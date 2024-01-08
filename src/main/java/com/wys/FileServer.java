package com.wys;

import com.wys.utils.FileUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * @Author wys
 * @Date 2024/1/5
 * @Description File传输服务端
 */
public class FileServer {

    public static void main(String[] args) throws IOException {
        //定义一个ServerSocket服务端对象，并为其绑定端口号
        ServerSocket server = new ServerSocket(8888);
        System.out.println("===========File服务端启动================");
        //对File来讲，每个Socket都需要一个Thread
        while (true) {
            //监听客户端Socket连接
            Socket socket = server.accept();
            System.out.println("qqqqq");
            new FileServerInputThread(socket).start();
            new FileServerOutputThread(socket).start();
        }

    }

    /**
     * File Server线程
     */
    static class FileServerInputThread extends Thread{
        //socket连接
        private Socket socket;
        public FileServerInputThread(Socket socket){
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
                        StringBuilder message = new StringBuilder();
                        String msg;
                        //从Buffer中读取信息，如果读取到信息则输出
                        System.out.println("等待客户端发送消息...");
                        while ((msg = bufferedReader.readLine()) != null) {
                            System.out.println(msg);
                            if ("END_OF_MESSAGE".equals(msg)) {
                                break;
                            }
                            message.append(msg);
                        }
                        //判断是否为结束信息,如果为空，则关闭连接
                        if (message.length()==0) {
                            System.out.println("客户端ggg下线了"+socket.getRemoteSocketAddress());
                            bufferedReader.close();
                            socket.close();
                            break;
                        }
                        msg = FileUtil.decode(message.toString());
                        System.out.println("收到客户端消息：\n" + msg);
                        if (FileUtil.isXML(msg)) {
                            System.out.println("dsfsdgsdg");
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
                        System.out.println("ddddd");

                    } catch (Exception e) {
                        System.out.println("客户端ggg下线了"+socket.getRemoteSocketAddress());
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
    static class FileServerOutputThread extends Thread {
        //socket连接
        private Socket socket;
        public FileServerOutputThread(Socket socket){
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
                            printStream.println(temp[1] + "\n");
                            //System.out.println(temp[1]);
                            outputStream.flush();
                            printStream.println("END_OF_MESSAGE\n");
                            outputStream.flush();
                            System.out.println("服务端发送消息成功");
                        }
                        if(socket.isClosed()){
                            System.out.println("客户端下aaaa线了"+socket.getRemoteSocketAddress());
                            break;
                        }
                    } catch (Exception e) {
                        System.out.println("客户端下aaaa线了"+socket.getRemoteSocketAddress());
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
}