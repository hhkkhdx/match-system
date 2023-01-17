package com.xuan.matchsystem.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageRequest implements Serializable {

    protected Long pageSize;

    protected Long pageNum;

}
