package com.xuan.matchsystem.mapper;

import com.xuan.matchsystem.model.domain.Team;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuan.matchsystem.model.domain.User;
import com.xuan.matchsystem.model.vo.TeamUserVO;
import com.xuan.matchsystem.model.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author 炫
* @description 针对表【team(队伍表)】的数据库操作Mapper
* @createDate 2023-01-15 14:57:02
* @Entity com.xuan.matchsystem.model.domain.Team
*/
@Mapper
public interface TeamMapper extends BaseMapper<Team> {
    List<UserVO> getUsersByTeamId(Long teamId);

    List<Team> listJoinTeams(Long userId);

    List<Team> listCreateTeams(Long userId);
}




