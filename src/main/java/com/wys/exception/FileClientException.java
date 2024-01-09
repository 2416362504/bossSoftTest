package com.wys.exception;

/**
 * @author wys
 * @version 1.0.0
 * @class CustomException$
 * @description
 * @aate 2024/1/9$  12.29$
 */
public class FileClientException extends Exception{
    public FileClientException(String message) {
        super(message);
        System.out.println(message);
    }
    public FileClientException(String message, Throwable cause) {
        super(message, cause);
        System.out.println(message+cause);
    }
}
