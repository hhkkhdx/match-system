package com.xuan.matchsystem.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 创建队伍请求参数
 * @author: xuan
 * @date: 2023/1/15 16:28
 **/
@Data
public class TeamAddRequest implements Serializable {

    /**
     * 队伍id
     **/
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 队伍状态, 0 - 公开, 1 - 私有, 2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;
}
