package com.xuan.matchsystem.service;

import com.xuan.matchsystem.model.domain.User;
import com.xuan.matchsystem.model.dto.TeamQuery;
import com.xuan.matchsystem.model.request.TeamUpdateRequest;
import com.xuan.matchsystem.model.vo.TeamUserVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class TeamServiceTest {


    @Autowired
    private TeamService teamService;

    @Test
    void testList() {
        TeamQuery teamQuery = new TeamQuery();
        List<TeamUserVO> teamUserVOList = teamService.listTeams(teamQuery, true);
        for (TeamUserVO teamUserVO : teamUserVOList) {
            System.out.println(teamUserVO);
        }
    }

    @Test
    void testUpdate() {

    }
}
