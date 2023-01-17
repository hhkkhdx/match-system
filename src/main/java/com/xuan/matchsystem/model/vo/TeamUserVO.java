package com.xuan.matchsystem.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @description: 队伍和用户信息封装类
 * @author: xuan
 * @date: 2023/1/15 17:40
 **/
@Data
public class TeamUserVO {

    /**
     * id
     */
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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户列表
     **/
    List<UserVO> userList;
}
