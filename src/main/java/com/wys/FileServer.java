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
                InputStream inputStream=socket.getInputStream();
                //转换为
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                String msg;
                //从Buffer中读取信息，如果读取到信息则输出
                while((msg=bufferedReader.readLine())!=null){
                    msg=FileUtil.decode(msg);
                    System.out.println("收到客户端消息：\n"+msg);
                    if(FileUtil.isXML(msg)) {
                        try (FileWriter writer = new FileWriter("E:\\IDEAWorkspace\\bossSoftTest\\src\\main\\resources\\2.xml")) { // 省略第二个参数的话，写入位置从文件开头开始
                            writer.write(msg);
                        }
                    }else if(FileUtil.isJSON(msg)){
                        try (FileWriter writer = new FileWriter("E:\\IDEAWorkspace\\bossSoftTest\\src\\main\\resources\\2.json")) { // 省略第二个参数的话，写入位置从文件开头开始
                            writer.write(msg);
                        }
                    }else {
                        System.out.println("数据不是json格式或者xml格式，请重新选择：\n");
                    }
                }
            } catch (Exception e) {
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
                while(true){
                    //定义本地的文件路径
                    String commad;
                    Scanner scanner = new Scanner(System.in);
                    commad = scanner.nextLine();
                    String [] temp = commad.split(" ");
                    //从socket中获取输出流
                    OutputStream outputStream = socket.getOutputStream();
                    PrintStream printStream = new PrintStream(outputStream);
                    //通过输出流对象向客户端传递信息
                    //outputStream.write(temp[1].getBytes());
                    printStream.println(temp[1]);
                    //System.out.println(temp[1]);
                    outputStream.flush();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}