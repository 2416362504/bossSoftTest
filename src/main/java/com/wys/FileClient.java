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
    public static void main(String[] args) throws Exception {
        //创建socket并根据IP地址与端口连接服务端

        System.out.println("===========File客户端启动================");
        while (true) {
            //通过输出流向服务端传递信息
            Scanner scanner=new Scanner(System.in);
            String filePath=scanner.nextLine();
            Socket socket = socketFactory.createSocket("127.0.0.1", 8888);
            InputStream inputStream = socket.getInputStream();
            if(filePath!=null||inputStream!=null) {
                new FileClient.FileClientInputThread().start();
                new FileClient.FileClientOutputThread().start();
            }
        }
    }

    static class FileClientInputThread extends Thread {
        @Override
        public void run() {
            try {
                Socket socket = socketFactory.createSocket("127.0.0.1", 8888);
                OutputStream outputStream=socket.getOutputStream();
                //从socket中获取字节输入流
                InputStream inputStream = socket.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                //读取服务端消息
                String msg;
                while ((msg = bufferedReader.readLine()) != null) {
                    System.out.println("客户端读取服务端消息");
                    System.out.println(msg);
                    System.out.println("收到服务端消息：" + msg);
                    byte[] data = FileUtil.readFileToByteArray(msg);
                    outputStream.write(data);
                    outputStream.flush();
                    // 发送传输结束的信号
                    socket.shutdownOutput();
                    inputStream.close();
                    outputStream.close();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class FileClientOutputThread extends Thread {

        public void run() {
            try {
                Socket socket = socketFactory.createSocket("127.0.0.1", 8888);
                OutputStream outputStream=socket.getOutputStream();

                byte[] data = FileUtil.readFileToByteArray(filePath);
                outputStream.write(data);
                outputStream.flush();
                // 发送传输结束的信号
                socket.shutdownOutput();
                outputStream.close();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}