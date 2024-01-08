package com.wys;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

import com.wys.factory.SocketFactory;
import com.wys.factory.impl.DefaultSocketFactory;
import com.wys.utils.FileUtil;

/**
 * @Author wys
 * @Date 2024/1/5
 * @Description File客户端
 */
public class FileClient {

    private static SocketFactory socketFactory = new DefaultSocketFactory();
    private static String userId = "1";
    public static void main(String[] args) throws IOException {

        System.out.println("===========File客户端启动================");

        //创建socket并根据IP地址与端口连接服务端
        Socket socket = new Socket("127.0.0.1", 8888);

        new FileClient.FileClientOutputThread(socket).start();
        new FileClient.FileClientInputThread(socket).start();
    }

    static class FileClientInputThread extends Thread {

        private Socket socket;
        public FileClientInputThread(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            try {
                OutputStream outputStream = socket.getOutputStream();
                //从socket中获取字节输入流
                InputStream inputStream = socket.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                while (true) {
                    try {
                        // 读取数据
                        StringBuilder message = new StringBuilder();
                        String msg;
                        //从Buffer中读取信息，如果读取到信息则输出
                        while ((msg = bufferedReader.readLine()) != null&&!socket.isClosed()) {
                            System.out.println(msg);
                            if ("END_OF_MESSAGE".equals(msg)) {
                                break;
                            }
                            message.append(msg);
                        }
                        if (message.length()==0) {
                            continue;
                        }
                        System.out.println("收到服务端请求：" + message);

                        byte[] data = FileUtil.readFileToByteArray(message.toString());
                        outputStream.write(data);
                        outputStream.write("\n".getBytes());
                        outputStream.flush();
                        outputStream.write("END_OF_MESSAGE".getBytes());
                        outputStream.write("\n".getBytes());
                        outputStream.flush();
                        System.out.println("根据请求，成功发送文件给服务器");
                    } catch (Exception e) {
                        System.out.println("我下hhdd线了");
                        outputStream.close();
                        inputStream.close();
                        socket.close();
                        break;
                    }
                }

            } catch (Exception e) {
                System.out.println("add");
                e.printStackTrace();
            }
        }
    }

    static class FileClientOutputThread extends Thread {

        private Socket socket;
        public FileClientOutputThread(Socket socket){
            this.socket = socket;
        }
        public void run() {
            try {
                OutputStream outputStream = socket.getOutputStream();
                Scanner scanner = new Scanner(System.in);
                //向服务端发送id
                outputStream.write((userId+"\n").getBytes());
                outputStream.flush();
                System.out.println("请输入文件路径或者输入exit退出登录：");
                while (true) {
                    if(scanner.hasNext()) {
                        String filePath = scanner.nextLine();
                        if (filePath.equals("exit")) {
                            System.out.println("mm我下线了");
                            outputStream.close();
                            socket.getInputStream().close();
                            socket.close();
                            break;
                        }
                        byte[] data = FileUtil.readFileToByteArray(filePath);
                        outputStream.write(data);
                        outputStream.write("\n".getBytes());
                        outputStream.flush();
                        outputStream.write("END_OF_MESSAGE".getBytes());
                        outputStream.write("\n".getBytes());
                        outputStream.flush();
                        System.out.println("发送文件成功");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}