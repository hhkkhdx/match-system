package com.xuan.matchsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.matchsystem.model.domain.UserTeam;
import com.xuan.matchsystem.service.UserTeamService;
import com.xuan.matchsystem.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 炫
* @description 针对表【user_team(用户队伍关系表)】的数据库操作Service实现
* @createDate 2023-01-15 14:57:14
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




