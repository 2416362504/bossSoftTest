package com.wys;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.wys.Factory.SocketFactory;
import com.wys.Factory.impl.DefaultSocketFactory;
import com.wys.utils.FileUtil;

/**
 * @Author wys
 * @Date 2024/1/5
 * @Description File客户端
 */
public class FileClient {

    private static SocketFactory socketFactory = new DefaultSocketFactory();
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
                while (true) {
                    OutputStream outputStream = socket.getOutputStream();
                    //从socket中获取字节输入流
                    InputStream inputStream = socket.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    // 读取数据
                    StringBuilder message = new StringBuilder();
                    String msg;
                    //从Buffer中读取信息，如果读取到信息则输出
                    while ((msg = bufferedReader.readLine()) != null) {
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

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
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
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    if(scanner.hasNext()) {
                        String filePath = scanner.nextLine();
                        if (filePath.equals("exit")) {
                            socket.close();
                        }
                        OutputStream outputStream = socket.getOutputStream();
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
                throw new RuntimeException(e);
            }
        }
    }

}