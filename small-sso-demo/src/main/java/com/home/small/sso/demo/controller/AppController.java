package com.home.small.sso.demo.controller;

import com.home.small.sso.client.constants.Oauth2Constant;
import com.home.small.sso.client.rpc.Result;
import com.home.small.sso.client.rpc.RpcAccessToken;
import com.home.small.sso.client.rpc.SsoUser;
import com.home.small.sso.client.utils.Oauth2Utils;
import com.home.small.sso.client.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author GTsung
 * @date 2021/10/31
 */
@RestController
@RequestMapping("/app")
public class AppController {

    @Value("${sso.server.url}")
    private String serverUrl;
    @Value("${sso.app.id}")
    private String appId;
    @Value("${sso.app.secret}")
    private String appSecret;

    /**
     * 初始页
     * @param request
     * @return
     */
    @RequestMapping
    public Result index(HttpServletRequest request) {
        SsoUser user = SessionUtils.getUser(request);
        return Result.createSuccess(user);
    }

    /**
     * 登录提交
     *
     * @param username
     * @param password
     * @param request
     * @return
     */
    @RequestMapping("/login")
    public Result login(
            @RequestParam(value = Oauth2Constant.USERNAME) String username,
            @RequestParam(value = Oauth2Constant.PASSWORD) String password,
            HttpServletRequest request) {
        Result<RpcAccessToken> result = Oauth2Utils.getAccessToken(serverUrl, appId, appSecret, username, password);
        if (!result.isSuccess()) {
            return result;
        }
        SessionUtils.setAccessToken(request, result.getData());
        return Result.createSuccess().setMessage("登录成功");
    }

}
