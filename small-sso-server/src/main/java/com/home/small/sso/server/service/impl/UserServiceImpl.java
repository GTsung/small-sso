package com.home.small.sso.server.service.impl;

import com.home.small.sso.client.rpc.Result;
import com.home.small.sso.client.rpc.SsoUser;
import com.home.small.sso.server.model.User;
import com.home.small.sso.server.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GTsung
 * @date 2021/10/31
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    /**
     * 可从数据库查询，这里同样写死
     */
    private static List<User> userList;

    static {
        userList = new ArrayList<>();
        userList.add(new User(1, "管理员", "admin", "123456"));
    }

    @Override
    public Result<SsoUser> login(String username, String password) {
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                if (user.getPassword().equals(password)) {
                    return Result.createSuccess(new SsoUser(user.getId(), user.getUsername()));
                } else {
                    return Result.createError("密码有误");
                }
            }
        }
        return Result.createError("用户不存在");
    }
}
