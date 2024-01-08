package com.wys.utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public class FileUtil {

    /*/**
     * @description: 字符编码
     * @author: wys
     * @date: 2024/01/08  19/52
     * @param:
     * @return:
     */
    public final static String ENCODING = "UTF-8";

    /**
     * @description: 通过路径读取文件内容并加密
     * @author: wys
     * @date: 2024/01/08  19/55
     * @param: [fileName]  文件路径
     * @return: byte[]
     */
    public static byte[] readFileToByteArray(String fileName) throws Exception {

        byte[] data = Files.readAllBytes(Paths.get(fileName));

        // 使用指定的编码读取文件内容
        byte[] bytes = encode(data);
        return bytes;
    }

    /**
     * @description: Base64编码
     * @author: wys
     * @date: 2024/01/08  19/56
     * @param: [data] 待编码数据
     * @return: byte[]
     */
    public static byte[] encode(byte[] data) throws Exception {

        // 执行编码
        byte[] b = Base64.encodeBase64(data);

        return b;
    }

    /**
     * @description: Base64安全编码<br>
     * @author: wys
     * @date: 2024/01/08  19/57
     * @param: [data]  待编码数据
     * @return: java.lang.String
     */
    public static String encodeSafe(String data) throws Exception {

        // 执行编码
        byte[] b = Base64.encodeBase64(data.getBytes(ENCODING), true);

        return new String(b, ENCODING);
    }

    /**
     * @description: Base64解码
     * @author: wys
     * @date: 2024/01/08  19/58
     * @param: [data]  待解码数据
     * @return: java.lang.String
     */
    public static String decode(String data) throws Exception {

        // 执行解码
        byte[] b = Base64.decodeBase64(data.getBytes(ENCODING));

        return new String(b, ENCODING);
    }
    /**
     * @description: 解析json和xml文件
     * @author: wys
     * @date: 2024/01/08  20/00
     * @param: [path]  文件路径
     * @return: void
     */
    public static void read(String path) throws Exception{
        File file = new File(path);
        try {
            if (file.isFile() && file.exists()) {
                if (path.endsWith(".xml")) {
                    // Read XML using DOM4J
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(file);
                    // Process the XML document here
                    // ...
                } else if (path.endsWith(".json")) {
                    // Read JSON using AstJson
                    String json = new String(Files.readAllBytes(Paths.get(path)));
                    JSONObject jsonObject = JSON.parseObject(json);
                    // Process the JSONObject here
                    // ...
                }
            } else {
                System.out.println("File does not exist or is not a file.");
            }
        } catch (DocumentException e) {
            System.out.println("Error reading XML file: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error reading JSON file: " + e.getMessage());
        }

    }

    /**
     * @description: 判断是否是xml文件
     * @author: wys
     * @date: 2024/01/08  20/01
     * @param: [data]  文件内容
     * @return: boolean
     */
    public static boolean isXML(String data) {
        // 判断是否以<开头
        if (data.startsWith("<")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @description: 判断是否是json文件
     * @author: wys
     * @date: 2024/01/08  20/02
     * @param: [data]  文件内容
     * @return: boolean
     */
    public static boolean isJSON(String data) {
        // 判断是否以{或[开头
        if (data.startsWith("{") || data.startsWith("[")) {
            // 判断是否以}或]结束
            if (data.endsWith("}") || data.endsWith("]")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * @description: 发送文件到服务器
     * @author: wys
     * @date: 2024/01/08  20/03
     * @param: [outputStream, filePath]  文件路径
     * @return: void
     */
    public static void sendDataToServer(OutputStream outputStream, String filePath) throws Exception {
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
