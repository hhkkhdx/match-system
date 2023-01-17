package com.xuan.matchsystem.mapper;

import com.xuan.matchsystem.model.domain.UserTeam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 炫
* @description 针对表【user_team(用户队伍关系表)】的数据库操作Mapper
* @createDate 2023-01-15 14:57:14
* @Entity com.xuan.matchsystem.model.domain.UserTeam
*/
@Mapper
public interface UserTeamMapper extends BaseMapper<UserTeam> {

}




