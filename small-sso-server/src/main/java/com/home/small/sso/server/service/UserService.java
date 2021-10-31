package com.home.small.sso.server.service;

import com.home.small.sso.client.rpc.Result;
import com.home.small.sso.client.rpc.SsoUser;

/**
 * @author GTsung
 * @date 2021/10/31
 */
public interface UserService {

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    Result<SsoUser> login(String username, String password);
}
