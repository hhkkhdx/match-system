package com.xuan.matchsystem.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 用户登录请求参数
 * @author: xuan
 * @date: 2022/12/21 22:58
 **/
@Data
public class UserLoginRequest implements Serializable {
    private String userAccount;
    private String userPassword;
}
