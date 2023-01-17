package com.xuan.matchsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuan.matchsystem.common.BaseResponse;
import com.xuan.matchsystem.common.ErrorCode;
import com.xuan.matchsystem.common.ResultUtils;
import com.xuan.matchsystem.constant.UserConstant;
import com.xuan.matchsystem.exception.BusinessException;
import com.xuan.matchsystem.model.domain.Team;
import com.xuan.matchsystem.model.domain.User;
import com.xuan.matchsystem.model.dto.TeamQuery;
import com.xuan.matchsystem.model.request.*;
import com.xuan.matchsystem.model.vo.TeamUserVO;
import com.xuan.matchsystem.service.TeamService;
import com.xuan.matchsystem.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @description: 用户接口
 * @author: xuan
 * @date: 2022/12/21 22:53
 **/
@RestController
//@CrossOrigin()
@RequestMapping("/team")
public class TeamController {

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }

    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(@RequestBody TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        boolean isAdmin = userService.isAdmin(loginUser);
        List<TeamUserVO> teamUserVOList = teamService.listTeams(teamQuery, isAdmin);
        return ResultUtils.success(teamUserVOList);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        boolean isAdmin = userService.isAdmin(loginUser);
        Boolean result = teamService.updateTeam(teamUpdateRequest, loginUser, isAdmin);
        return ResultUtils.success(result);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/exit")
    public BaseResponse<Boolean> exitTeam(@RequestBody TeamExitRequest teamExitRequest, HttpServletRequest request) {
        if (teamExitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamExitRequest.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Boolean result = teamService.exitTeam(teamId, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody TeamDeleteRequest teamDeleteRequest, HttpServletRequest request) {
        if (teamDeleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamDeleteRequest.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Boolean result = teamService.deleteTeam(teamId, loginUser);
        return ResultUtils.success(result);
    }

    @GetMapping("/list/join")
    public BaseResponse<List<TeamUserVO>> listJoinTeams(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<TeamUserVO> teamList = teamService.listJoinTeams(loginUser);
        return ResultUtils.success(teamList);
    }

    @GetMapping("/list/create")
    public BaseResponse<List<TeamUserVO>> listCreateTeams(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<TeamUserVO> teamList = teamService.listCreateTeams(loginUser);
        return ResultUtils.success(teamList);
    }
}
