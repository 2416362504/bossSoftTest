package com.wys.exception;

import com.wys.FileServerObserver;

/**
 * @author wys
 * @version 1.0.0
 * @class FileServerException$
 * @description
 * @aate 2024/1/9$  14:02$
 */
public class FileServerException extends Exception{
    public FileServerException(String id) {
        super(id);
        if (FileServerObserver.userIdList.contains(id)) {
            FileServerObserver.userIdList.remove(id);
            FileServerObserver.socketMap.remove(id);
        }
        System.out.println("客户端"+id+"下线了");
    }
    public FileServerException(String message, Throwable cause) {
        super(message, cause);
        System.out.println(message+cause.getMessage());
    }
}
