package com.xuan.matchsystem.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamListJoinRequest implements Serializable {

    private Long userId;
}
