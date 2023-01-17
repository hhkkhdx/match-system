package com.xuan.matchsystem.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * @description:  用户包装类
 * @author: xuan 
 * @date: 2023/1/15 17:44
 **/
@Data
public class UserVO {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户状态, 0-正常
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户角色，0-普通用户，1-管理员
     */
    private Integer userRole;

    /**
     * 标签列表
     */
    private String tags;

}
