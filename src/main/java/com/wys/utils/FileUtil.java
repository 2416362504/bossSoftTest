package com.wys.utils;

import java.io.File;
import java.io.IOException;
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

    /**
     * 字符编码
     */
    public final static String ENCODING = "UTF-8";

    public static byte[] readFileToByteArray(String fileName) throws Exception {

        byte[] data = Files.readAllBytes(Paths.get(fileName));

        // 使用指定的编码读取文件内容
        byte[] bytes = encode(data);
        return bytes;
    }

    /**
     * Base64编码
     *
     * @param data 待编码数据
     * @return String 编码数据
     * @throws Exception
     */
    public static byte[] encode(byte[] data) throws Exception {

        // 执行编码
        byte[] b = Base64.encodeBase64(data);

        return b;
    }

    /**
     * Base64安全编码<br>
     * 遵循RFC 2045实现
     *
     * @param data
     *            待编码数据
     * @return String 编码数据
     *
     * @throws Exception
     */
    public static String encodeSafe(String data) throws Exception {

        // 执行编码
        byte[] b = Base64.encodeBase64(data.getBytes(ENCODING), true);

        return new String(b, ENCODING);
    }

    /**
     * Base64解码
     *
     * @param data 待解码数据
     * @return String 解码数据
     * @throws Exception
     */
    public static String decode(String data) throws Exception {

        // 执行解码
        byte[] b = Base64.decodeBase64(data.getBytes(ENCODING));

        return new String(b, ENCODING);
    }


    /*
    xml和json的解析
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

    public static boolean isXML(String data) {
        // 判断是否以<开头
        if (data.startsWith("<")) {
            // 判断是否以</结束
            if (data.endsWith(">")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

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


}
