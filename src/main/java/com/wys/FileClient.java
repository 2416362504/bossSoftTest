package com.wys;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import com.wys.exception.FileClientException;
import com.wys.utils.FileUtil;

/**
 * @Author wys
 * @Date 2024/1/5
 * @Description File客户端
 */
public class FileClient {
    private static final String END_OF_MESSAGE = "END_OF_MESSAGE";
    private static String userId = "1";
    public static void main(String[] args) {

        System.out.println("===========File客户端启动================");

        //创建socket并根据IP地址与端口连接服务端
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", 8888);
            new FileClient.FileClientOutputThread(socket).start();
            new FileClient.FileClientInputThread(socket).start();
        } catch (Exception e) {
            FileUtil.logError(e);
        }
    }

    /*/**
     * @description: 客户端的输入线程类
     * @author: wys
     * @date: 2024/01/08  20/03
     */
    static class FileClientInputThread extends Thread {
        private final Socket socket;
        public FileClientInputThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (OutputStream outputStream = socket.getOutputStream();
                 InputStream inputStream = socket.getInputStream()) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                while (true) {
                    List<String> message = FileUtil.readMessage(bufferedReader);
                    if (message.isEmpty()) {
                        continue;
                    }

                    try {
                        FileUtil.handleClientMessage(outputStream, message);
                    } catch (Exception e) {
                        FileUtil.logError(e);
                        new FileClientException("处理客户端消息时发生异常", e);
                    }
                }
            } catch (Exception e) {
                new FileClientException("",e);
            }
        }
    }

    /**
     * @description: 客户端的输出线程类
     * @author: wys
     * @date: 2024/01/08  20/03
     */
    static class FileClientOutputThread extends Thread {

        private final Socket socket;
        public FileClientOutputThread(Socket socket){
            this.socket = socket;
        }
        @Override
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
                        if ("exit".equals(filePath)) {
                            new FileClientException("我退出登录了");
                            outputStream.close();
                            socket.getInputStream().close();
                            socket.close();
                            break;
                        }
                        String[] msg =filePath.split(" ");
                        if(msg.length==1) {
                            System.out.println("开始发送文件");
                            byte[] data = FileUtil.readFileToByteArray(filePath);
                            outputStream.write(data);
                            outputStream.write("\n".getBytes());
                            outputStream.flush();
                            outputStream.write(END_OF_MESSAGE.getBytes());
                            outputStream.write("\n".getBytes());
                            outputStream.flush();
                            System.out.println("发送文件成功");
                        }
                        if(msg.length==3){
                            System.out.println("开始发送文件");
                            outputStream.write(msg[1].getBytes());
                            outputStream.write("\n".getBytes());
                            outputStream.flush();
                            byte[] data = FileUtil.readFileToByteArray(msg[2]);
                            outputStream.write(data);
                            outputStream.write("\n".getBytes());
                            outputStream.flush();
                            outputStream.write(END_OF_MESSAGE.getBytes());
                            outputStream.write("\n".getBytes());
                            outputStream.flush();
                            System.out.println("发送文件成功");
                        }
                    }
                }
            } catch (Exception e) {
                new FileClientException("",e);
            }
        }
    }

}