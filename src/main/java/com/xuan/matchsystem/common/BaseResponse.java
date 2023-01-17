package com.xuan.matchsystem.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 通用返回类
 * @author: xuan
 * @date: 2022/12/22 18:50
 **/
@Data
public class BaseResponse<T> implements Serializable {
    private int code;
    private String message;
    private String description;
    private T data;

    public BaseResponse() {
    }

    public BaseResponse(int code, T data) {
        this(code, "ok", "操作成功", data);
    }

    public BaseResponse(int code, String message, T data) {
        this(code, message, "操作成功", data);
    }

    public BaseResponse(int code, String message, String description, T data) {
        this.code = code;
        this.message = message;
        this.description = description;
        this.data = data;
    }

    public BaseResponse(ErrorCode responseCode) {
        this(responseCode.getCode(), responseCode.getMessage(), responseCode.getDescription(), null);
    }

    public BaseResponse(ErrorCode responseCode, String message) {
        this(responseCode.getCode(), message, responseCode.getDescription(), null);
    }
    public BaseResponse(ErrorCode responseCode, String message, String description) {
        this(responseCode.getCode(), message, description, null);
    }


}
