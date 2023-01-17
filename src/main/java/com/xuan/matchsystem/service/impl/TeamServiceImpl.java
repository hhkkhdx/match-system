package com.xuan.matchsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.matchsystem.common.ErrorCode;
import com.xuan.matchsystem.exception.BusinessException;
import com.xuan.matchsystem.mapper.UserTeamMapper;
import com.xuan.matchsystem.model.domain.Team;
import com.xuan.matchsystem.model.domain.User;
import com.xuan.matchsystem.model.domain.UserTeam;
import com.xuan.matchsystem.model.dto.TeamQuery;
import com.xuan.matchsystem.model.enums.TeamStatusEnum;
import com.xuan.matchsystem.model.request.TeamJoinRequest;
import com.xuan.matchsystem.model.request.TeamUpdateRequest;
import com.xuan.matchsystem.model.vo.TeamUserVO;
import com.xuan.matchsystem.model.vo.UserVO;
import com.xuan.matchsystem.service.TeamService;
import com.xuan.matchsystem.mapper.TeamMapper;
import com.xuan.matchsystem.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.BuilderException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
* @author 炫
* @description 针对表【team(队伍表)】的数据库操作Service实现
* @createDate 2023-01-15 14:57:02
*/
@Service
@Slf4j
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{


    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private UserTeamService userTeamService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        // 参数不能为空
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录状态不能为空
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        final long userId = loginUser.getId();
        // 最大人数 >= 1 且 <= 20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        }
        // 队伍标题不能为空 且长度不能大于 20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }
        // 队伍描述长度不能大于 512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        // status 不传默认为0
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }
        // 如果status为加密状态 需要有密码 且密码长度不能大于 32
        String password = team.getPassword();
        if (statusEnum.equals(TeamStatusEnum.SECRET) && (StringUtils.isBlank(password) || password.length() > 32)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不满足要求");
        }
        // 过期时间不能小于当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间 > 当前时间");
        }
        // 每个用户最多创建/加入 5 个队伍
        // todo 有bug 可能同时创建100个队伍 -> 分布式锁
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        int hasTeamCount = this.count(queryWrapper);
        if (hasTeamCount >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建/加入5个队伍");
        }
        // 插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId = team.getId();
        if (!result || teamId == null) {
            throw new BusinessException(ErrorCode.DB_ERROR, "创建队伍失败");
        }
        // 插入用户 => 队伍关系 到用户队伍关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        userTeamService.save(userTeam);
        return teamId;
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, Boolean isAdmin) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        // 有id则查id
        Long id = teamQuery.getId();
        if (id != null && id > 0) {
            queryWrapper.eq("id", id);
        }
        // 有name则查name
        String name = teamQuery.getName();
        if (StringUtils.isNotBlank(name) && name.length() <= 20) {
            queryWrapper.like("name", name);
        }
        // 有描述则查描述
        String description = teamQuery.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() <= 512) {
            queryWrapper.like("description", description);
        }
        // 有userId则查userId
        Long userId = teamQuery.getUserId();
        if (userId != null && userId > 0) {
            queryWrapper.eq("user_id", userId);
        }
        // 非管理员只可以查询公开的队伍 若无传递status参数 默认为public
        Integer status = teamQuery.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if (teamStatusEnum == null) {
            teamStatusEnum = TeamStatusEnum.PUBLIC;
        }
        if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum) && Boolean.FALSE.equals(isAdmin)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限搜索私密队伍");
        }
        queryWrapper.eq("status", teamStatusEnum.getValue());
        // 有最大人数则查最大人数
        Integer maxNum = teamQuery.getMaxNum();
        if (maxNum != null && maxNum > 0) {
            queryWrapper.eq("max_num", maxNum);
        }
        // 不展示已过期的队伍
        queryWrapper.and(qw -> qw.gt("expire_time", new Date()).or().isNull("expire_time"));
        // 分页 若无传递分页相关参数 默认查询第一页 10条记录
        Long pageNum = Optional.ofNullable(teamQuery.getPageNum()).orElse(1L);
        Long pageSize = Optional.ofNullable(teamQuery.getPageSize()).orElse(10L);
        Page<Team> pageList = this.page(new Page<>(pageNum, pageSize), queryWrapper);
        log.info("\n\n " + pageList.getRecords());
        List<Team> teamList = pageList.getRecords();
        // 没查到返回空集合
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        // 关联查询用户信息
        return relateQueryUserMsg(teamList);
    }

    @Override
    public Boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser, boolean isAdmin) {
        // 请求参数不能为空
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 未登录不能修改
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // id 必须有且合法
        Long id = teamUpdateRequest.getId();
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 查询要修改的队伍
        Team oldTeam = this.getById(id);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        // 过期了不能修改
        Date expireTime = oldTeam.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.NULL_ERROR,  "队伍已过期");
        }
        // 只有创建队伍的用户或管理员可修改队伍信息
        Long userId = oldTeam.getUserId();
        if (!userId.equals(loginUser.getId()) && Boolean.FALSE.equals(isAdmin)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, team);
        Integer status = teamUpdateRequest.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        // 队伍状态不能为空
        if (teamStatusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 如果要修改的队伍为public 或不修改状态(public) 不能有密码
        if (teamStatusEnum.equals(TeamStatusEnum.PUBLIC)) {
            team.setPassword("");
        }
        if (teamStatusEnum.equals(TeamStatusEnum.PRIVATE) && !isAdmin) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        // 如果要修改队伍为secret 或不修改状态(secret) 必须要有密码
        if (teamStatusEnum.equals(TeamStatusEnum.SECRET) && teamUpdateRequest.getPassword() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "缺少密码");
        }
        return this.updateById(team);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        // todo 有问题 用户1s内疯狂点击可重复加入
        // 请求参数不能为空
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 需要登录
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // 请求参数teamId不能为空
        Long teamId = teamJoinRequest.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 根据teamId查询队伍信息
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        }
        // 获取查询到的队伍状态
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        // 如果队伍状态为私密 禁止加入
        if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态为私密, 禁止加入");
        }
        // 如果队伍状态为加密 需要匹配密码
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum)) {
            String password = teamJoinRequest.getPassword();
            if (StringUtils.isBlank(password) || !team.getPassword().equals(password)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不匹配");
            }
        }
        // 根据teamId查询该队伍的用户人数
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("team_id", teamId);
        int userCount = userTeamService.count(queryWrapper);
        // 队伍人数已达上限 禁止加入
        if (userCount >= team.getMaxNum()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数已达上限");
        }
        // 判断队伍是否已过期 若已过期 禁止加入
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        // 获取当前登录的用户id
        Long userId = loginUser.getId();
        // 查询该用户 队伍数量是否已达上限
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        int teamCount = userTeamService.count(queryWrapper);
        if (teamCount >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户的队伍数量已达上限");
        }
        // 判断是否重复加入队伍
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("team_id", teamId);
        UserTeam uTeam = userTeamService.getOne(queryWrapper);
        if (uTeam != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "重复加入队伍");
        }
        // 创建用户-队伍关系
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        // 插入用户-队伍关系到用户队伍关系表
        return userTeamService.save(userTeam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean exitTeam(Long teamId, User loginUser) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // 队伍必须存在 并且没过期
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        }
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        }
        // 查询该用户是否在该队伍
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("team_id", teamId);
        userTeamQueryWrapper.eq("user_id", userId);
        UserTeam userTeam = userTeamService.getOne(userTeamQueryWrapper);
        // 用户未加入队伍
        if (userTeam == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户未加入该队伍");
        }
        // 根据id取前两条数据
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("team_id", teamId);
        userTeamQueryWrapper.last("order by id asc limit 2");
        List<UserTeam> userTeams = userTeamService.list(userTeamQueryWrapper);
        if (CollectionUtils.isEmpty(userTeams)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 如果队伍人数为1 退出后 解散队伍
        if (userTeams.size() == 1) {
            this.removeById(teamId);
            userTeamService.removeById(userId);
        }
        else {
            // 如果退出的用户是队长 根据加入时间顺位转移队长
            if (team.getUserId().equals(userId)) {
                Long deleteUserId = userTeams.get(0).getId();
                userTeamService.removeById(deleteUserId);
                UserTeam nextTeamLeaderId = userTeams.get(1);
                Long nextTeamLeaderUserId = nextTeamLeaderId.getUserId();
                // 更新当前队伍的队长
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeaderUserId);
                this.updateById(updateTeam);
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteTeam(Long teamId, User loginUser) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = loginUser.getId();
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        }
        Long leaderId = team.getUserId();
        // 只有队长可以解散队伍
        if (!userId.equals(leaderId)) {
            throw new BusinessException(ErrorCode.FORBIDDER, "无权限解散队伍");
        }
        // 移除所有队伍-用户关系
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("team_id", teamId);
        userTeamService.remove(userTeamQueryWrapper);
        // 删除队伍
        this.removeById(teamId);

        return true;
    }

    @Override
    public List<TeamUserVO> listJoinTeams(User loginUser) {
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = loginUser.getId();
        List<Team> teamList = teamMapper.listJoinTeams(userId);
        // 关联查询用户信息
        return this.relateQueryUserMsg(teamList);

    }

    @Override
    public List<TeamUserVO> listCreateTeams(User loginUser) {
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = loginUser.getId();
        List<Team> teamList = teamMapper.listCreateTeams(userId);
        // 关联查询用户信息
        return this.relateQueryUserMsg(teamList);
    }

    private List<TeamUserVO> relateQueryUserMsg(List<Team> teamList) {
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        for (Team team: teamList) {
            Long teamId = team.getId();
            List<UserVO> userVOList = teamMapper.getUsersByTeamId(teamId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            teamUserVO.setUserList(userVOList);
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }


    // todo 根据标签匹配用户 相似度


}




