package com.xuan.matchsystem.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:  解散队伍请求参数
 * @author: xuan
 * @date: 2023/1/16 14:39
 **/
@Data
public class TeamDeleteRequest implements Serializable {

    private Long teamId;

}
