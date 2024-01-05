package com.wys;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import com.wys.utils.FileUtil;

/**
 * @Author wys
 * @Date 2024/1/5
 * @Description File客户端
 */
public class FileClient {

    public static void main(String[] args) throws Exception {
        //创建socket并根据IP地址与端口连接服务端
        Socket socket=new Socket("127.0.0.1",8888);
        System.out.println("===========File客户端启动================");

        new FileClient.FileClientInputThread(socket).start();
        new FileClient.FileClientOutputThread(socket).start();
    }

    static class FileClientInputThread extends Thread {
        //socket连接
        private Socket socket;

        public FileClientInputThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                //从socket中获取字节输入流
                InputStream inputStream = socket.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                //读取服务端消息
                String msg;
                while ((msg = bufferedReader.readLine()) != null) {
                    System.out.println("客户端读取服务端消息");
                    System.out.println(msg);
                    System.out.println("收到服务端消息：" + msg);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class FileClientOutputThread extends Thread {
        private Socket socket;

        public FileClientOutputThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                OutputStream outputStream=socket.getOutputStream();
                //通过输出流向服务端传递信息
                Scanner scanner=new Scanner(System.in);
                String filePath=scanner.nextLine();
                byte[] data = FileUtil.readFileToByteArray(filePath);
                outputStream.write(data);
                outputStream.flush();
                // 发送传输结束的信号
                socket.shutdownOutput();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}