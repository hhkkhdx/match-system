package com.xuan.matchsystem.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:  加入队伍请求参数
 * @author: xuan
 * @date: 2023/1/15 22:18
 **/
@Data
public class TeamJoinRequest implements Serializable {

    private Long teamId;

    private String password;

}
