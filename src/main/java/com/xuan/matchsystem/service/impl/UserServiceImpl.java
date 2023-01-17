package com.xuan.matchsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xuan.matchsystem.common.ErrorCode;
import com.xuan.matchsystem.constant.UserConstant;
import com.xuan.matchsystem.exception.BusinessException;
import com.xuan.matchsystem.model.domain.User;
import com.xuan.matchsystem.service.UserService;
import com.xuan.matchsystem.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
* @author 炫
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2022-12-21 15:58:10
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 盐值
    private static final String SALT = "xuan";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        // 非空 长度校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            // todo: 修改为自定义异常
//            return -1;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4 || userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码与确认密码不一致");
        }
        // 账户不能包含特殊字符
        Pattern pattern= Pattern.compile("^[a-zA-Z0-9_]+$");
        Matcher matcher = pattern.matcher(userAccount);
        if (!matcher.matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号包含特殊字符");
        }
        // 账户不能重复
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_account", userAccount);
        int count = userMapper.selectCount(userQueryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能重复");
        }

        // 2. 加密
        String newPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));

        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(newPassword);
        boolean result = this.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.DB_ERROR, "数据库操作异常: 添加用户信息失败");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        // 非空 长度校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "账号或密码为空");
        }
        if (userAccount.length() < 4 || userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码长度过短");
        }
        // 账户不能包含特殊字符
        Pattern pattern= Pattern.compile("^[a-zA-Z0-9_]+$");
        Matcher matcher = pattern.matcher(userAccount);
        if (!matcher.matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号包含特殊字符");
        }
        // 2. 查询用户是否存在
        String newPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_account", userAccount).eq("user_password", newPassword);
        User user = userMapper.selectOne(userQueryWrapper);
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }
        // 3. 用户脱敏
        User safetyUser = toSafetyUser(user);
        // 4. 记录用户的登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    @Override
    public List<User> searchUsers(String username) {
        if (StringUtils.isAnyBlank(username)) {
            return userMapper.selectList(null).stream().map(this::toSafetyUser).collect(Collectors.toList());
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.like("username", username);
        List<User> users = userMapper.selectList(userQueryWrapper);
        return users.stream().map(this::toSafetyUser).collect(Collectors.toList());
    }

    /**
     * @description: 用户脱敏
     * @author: xuan
     * @date: 2022/12/22 0:14
     **/
    @Override
    public User toSafetyUser(User user) {
        if (user == null) return null;
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setTags(user.getTags());
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public List<User> searchUsersByTags(List<String> tags) {
        if (tags.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 1. 先查询所有
        List<User> users = userMapper.selectList(queryWrapper);
        // 2. 在内存中处理
        Gson gson = new Gson();
        return users.stream().filter(user -> {
            String tagStr = user.getTags();
            if (StringUtils.isBlank(tagStr)) {
                return false;
            }
            Set<String> tempTagsSet = gson.fromJson(tagStr, new TypeToken<Set<String>>(){}.getType());
            tempTagsSet = Optional.ofNullable(tempTagsSet).orElse(new HashSet<>());
            for (String tagName: tags) {
                if (!tempTagsSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::toSafetyUser).collect(Collectors.toList());
    }

    @Override
    public Integer updateUser(User user, User loginUser) {
        Long userId = user.getId();
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 如果是管理员 允许更新其它用户
        // 如果不是管理员 只允许更新自己的信息
        if (!isAdmin(loginUser) && !Objects.equals(loginUser.getId(), userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return userMapper.updateById(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
    }

    /**
     * @description: SQL查询的方式
     * @author: xuan
     * @date: 2023/1/4 12:59
     **/
    @Deprecated
    public List<User> searchUsersByTagsBySQL(List<String> tags) {
        if (tags.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for (String s: tags) {
            queryWrapper = queryWrapper.like("tags", s);
        }
        List<User> users = userMapper.selectList(queryWrapper);
        return users.stream().map(this::toSafetyUser).collect(Collectors.toList());
    }


    /**
     * @description: 鉴权, 是否为管理员
     * @author: xuan
     * @date: 2022/12/21 23:53
     **/
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        return user != null && user.getUserRole() == UserConstant.ADMIN_ROLE;
    }

    /**
     * @description: 鉴权, 是否为管理员
     * @author: xuan
     * @date: 2022/12/21 23:53
     **/
    @Override
    public boolean isAdmin(User user) {
        return user != null && user.getUserRole() == UserConstant.ADMIN_ROLE;
    }


    @Override
    public Page<User> recommendUsers(long pageNum, long pageSize, HttpServletRequest request) {
        User loginUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // 如果缓存中有 从缓存中获取
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        String key = String.format("matchsystem:user:recommend:%s", loginUser.getId());
        Page<User> userPage = (Page<User>) opsForValue.get(key);
        if (userPage != null) {
            return userPage;
        }
        // 缓存中没有 查询数据库 并写入缓存
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        try {
            opsForValue.set(key, userPage, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("redis set error: ", e);
        }
        return userPage;
    }
}
