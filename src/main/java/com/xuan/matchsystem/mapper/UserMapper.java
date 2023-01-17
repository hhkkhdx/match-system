package com.xuan.matchsystem.mapper;

import com.xuan.matchsystem.model.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author 炫
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2022-12-21 15:58:10
* @Entity com.xuan.matchsystem.model.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




