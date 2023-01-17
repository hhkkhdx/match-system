package com.xuan.matchsystem.mapper;

import com.xuan.matchsystem.model.domain.User;
import com.xuan.matchsystem.model.vo.UserVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/*
 * @description: 队伍服务测试
 * @author: xuan
 * @date: 2022/12/21 16:04
 * @param:
 * @return:
 **/
@SpringBootTest
class TeamMapperTest {


    @Autowired
    private TeamMapper teamMapper;

    @Test
    void testList() {
        List<UserVO> userVOList = teamMapper.getUsersByTeamId(2L);
        System.out.println(userVOList);
    }

}
