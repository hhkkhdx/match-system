package com.xuan.matchsystem.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 用户注册请求参数
 * @author: xuan
 * @date: 2022/12/21 22:58
 **/
@Data
public class UserRegisterRequest implements Serializable {
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String userCode;
}
