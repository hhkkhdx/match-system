package com.xuan.matchsystem.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:  退出队伍请求参数
 * @author: xuan
 * @date: 2023/1/16 12:55
 **/
@Data
public class TeamExitRequest implements Serializable {

    private Long teamId;

}
