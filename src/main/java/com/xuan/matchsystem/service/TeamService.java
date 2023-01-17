package com.xuan.matchsystem.service;

import com.xuan.matchsystem.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xuan.matchsystem.model.domain.User;
import com.xuan.matchsystem.model.dto.TeamQuery;
import com.xuan.matchsystem.model.request.TeamJoinRequest;
import com.xuan.matchsystem.model.request.TeamUpdateRequest;
import com.xuan.matchsystem.model.vo.TeamUserVO;

import java.util.List;

/**
* @author 炫
* @description 针对表【team(队伍表)】的数据库操作Service
* @createDate 2023-01-15 14:57:02
*/
public interface TeamService extends IService<Team> {


    /**
     * @description: 创建队伍
     * @author: xuan
     * @date: 2023/1/15 15:39
     **/
    long addTeam(Team team, User loginUser);

    /**
     * @description: 搜索队伍
     * @author: xuan
     * @date: 2023/1/15 19:09
     **/
    List<TeamUserVO> listTeams(TeamQuery teamQuery, Boolean isAdmin);

    /**
     * @description: 修改队伍
     * @author: xuan
     * @date: 2023/1/15 21:15
     **/
    Boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser, boolean isAdmin);

    /**
     * @description: 加入队伍
     * @author: xuan
     * @date: 2023/1/15 22:21
     **/
    Boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * @description:  退出队伍
     * @author: xuan
     * @date: 2023/1/16 12:23
     **/
    Boolean exitTeam(Long teamId, User loginUser);

    /**
     * @description:  解散队伍
     * @author: xuan
     * @date: 2023/1/16 14:39
     **/
    Boolean deleteTeam(Long teamId, User loginUser);

    /**
     * @description: 列出所有已加入的队伍
     * @author: xuan
     * @date: 2023/1/16 15:12
     **/
    List<TeamUserVO> listJoinTeams(User loginUser);

    /**
     * @description: 列出所有创建的队伍
     * @author: xuan
     * @date: 2023/1/16 15:23
     **/
    List<TeamUserVO> listCreateTeams(User loginUser);
}
