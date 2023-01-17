package com.xuan.matchsystem.mapper;

import com.xuan.matchsystem.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/*
 * @description: 用户服务测试
 * @author: xuan
 * @date: 2022/12/21 16:04
 * @param:
 * @return:
 **/
@SpringBootTest
class UserMapperTest {


    @Autowired
    private UserMapper userMapper;

    @Test
    void testInsert() {
        User user = new User();
        user.setUsername("xuan");
        user.setUserAccount("admin");
        user.setUserPassword("123123");
        user.setGender(0);
        user.setPhone("123");
        user.setEmail("456");
        int i = userMapper.insert(user);
        Assertions.assertEquals(1, i);
    }

}
