package com.xuan.matchsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuan.matchsystem.constant.UserConstant;
import com.xuan.matchsystem.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 炫
 * @description 针对表【user(用户表)】的数据库操作Service
 * @createDate 2022-12-21 15:58:10
 */
public interface UserService extends IService<User> {

    /**
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 确认密码
     * @description: 用户注册方法
     * @author: xuan
     * @date: 2022/12/21 16:42
     * @return: long 用户id
     **/
    long userRegister(String userAccount, String userPassword, String checkPassword);


    /**
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request      request
     * @description: 用户登录
     * @author: xuan
     * @date: 2022/12/21 22:13
     * @param: [java.lang.String, java.lang.String, java.lang.String]
     * @return: com.xuan.matchsystem.model.domain.User 返回脱敏后的用户信息
     **/
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * @param username 用户名
     * @description: 根据用户名搜索用户
     * @author: xuan
     * @date: 2022/12/21 23:28
     * @return: java.util.List<com.xuan.matchsystem.model.domain.User>
     **/
    List<User> searchUsers(String username);

    /**
     * @description: 用户脱敏
     * @author: xuan
     * @date: 2022/12/22 0:16
     **/
    User toSafetyUser(User user);

    /**
     * @description: 用户注销
     * @author: xuan
     * @date: 2022/12/22 18:00
     **/
    int userLogout(HttpServletRequest request);

    /**
     * @description: 根据标签搜索用户
     * @param tags 标签列表
     * @author: xuan
     * @date: 2023/1/3 22:23
     **/
    List<User> searchUsersByTags(List<String> tags);

    /**
     * @description: 修改用户信息
     * @author: xuan
     * @date: 2023/1/5 11:05
     **/
    Integer updateUser(User user, User loginUser);

    /**
     * @description: 获取当前登录的用户
     * @author: xuan
     * @date: 2023/1/5 11:09
     **/
    User getLoginUser(HttpServletRequest request);


    /**
     * @description: 鉴权, 是否为管理员
     * @author: xuan
     * @date: 2022/12/21 23:53
     **/
    boolean isAdmin(HttpServletRequest request);

    /**
     * @description: 鉴权, 是否为管理员
     * @author: xuan
     * @date: 2022/12/21 23:53
     **/
    boolean isAdmin(User user);

    /**
     * @description: 首页 推荐用户
     * @author: xuan
     * @date: 2023/1/5 20:47
     **/
    Page<User> recommendUsers(long pageNum, long pageSize, HttpServletRequest request);
}
