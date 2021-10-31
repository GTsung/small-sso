package com.home.small.sso.server.controller;

import com.home.small.sso.client.constants.Oauth2Constant;
import com.home.small.sso.client.constants.SsoConstant;
import com.home.small.sso.client.rpc.Result;
import com.home.small.sso.client.rpc.SsoUser;
import com.home.small.sso.server.constant.AppConstant;
import com.home.small.sso.server.service.AppService;
import com.home.small.sso.server.service.UserService;
import com.home.small.sso.server.session.CodeManager;
import com.home.small.sso.server.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author GTsung
 * @date 2021/10/31
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private CodeManager codeManager;

    @Autowired
    private AppService appService;

    @Autowired
    private UserService userService;

    /**
     * 跳转到登录页或者生成授权码重定向
     *
     * @param redirectUri
     * @param appId
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(method = RequestMethod.GET)
    public String login(
            @RequestParam(value = SsoConstant.REDIRECT_URI) String redirectUri,
            @RequestParam(value = Oauth2Constant.APP_ID) String appId,
            HttpServletRequest request) throws UnsupportedEncodingException {
        String tgt = sessionManager.getTgt(request);
        if (StringUtils.isEmpty(tgt)) {
            // 转发到登录页，url地址不会改变
            return goLoginPath(redirectUri, appId, request);
        }
        // 生成授权码并重定向
        return generateCodeAndRedirect(redirectUri, tgt);
    }

    /**
     * 登录网页提交登陆操作
     * @param redirectUri
     * @param appId
     * @param username
     * @param password
     * @param request
     * @param response
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(method = RequestMethod.POST)
    public String login(
            @RequestParam(value = SsoConstant.REDIRECT_URI) String redirectUri,
            @RequestParam(value = Oauth2Constant.APP_ID) String appId,
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

        if (!appService.exists(appId)) {
            request.setAttribute("errorMessage", "非法应用");
            return goLoginPath(redirectUri, appId, request);
        }

        Result<SsoUser> result = userService.login(username, password);
        if (!result.isSuccess()) {
            request.setAttribute("errorMessage", result.getMessage());
            return goLoginPath(redirectUri, appId, request);
        }
        // 生成TGT或者延长TGT的时效或者更新缓存中TGT对应的用户信息
        String tgt = sessionManager.setUser(result.getData(), request, response);
        return generateCodeAndRedirect(redirectUri, tgt);
    }

    /**
     * 转发至登录页
     *
     * @param redirectUri
     * @param appId
     * @param request
     * @return
     */
    private String goLoginPath(String redirectUri, String appId, HttpServletRequest request) {
        // 将重定向地址和appId发送至页面
        request.setAttribute(SsoConstant.REDIRECT_URI, redirectUri);
        request.setAttribute(Oauth2Constant.APP_ID, appId);
        return AppConstant.LOGIN_PATH;
    }

    /**
     * 生成授权码之后重定向到客户端
     *
     * @param redirectUri
     * @param tgt
     * @return
     */
    private String generateCodeAndRedirect(String redirectUri, String tgt) throws UnsupportedEncodingException {
        // 生成授权码，缓存code与TGT的对应关系
        String code = codeManager.generate(tgt, true, redirectUri);
        // 带着code授权码重定向到客户端
        return "redirect:" + authRedirectUri(redirectUri, code);
    }

    /**
     * 为重定向页面url添加code授权码参数
     *
     * @param redirectUri
     * @param code
     * @return
     * @throws UnsupportedEncodingException
     */
    private String authRedirectUri(String redirectUri, String code) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder(redirectUri);
        if (redirectUri.indexOf("?") > -1) {
            sb.append("&");
        } else {
            sb.append("?");
        }
        sb.append(Oauth2Constant.AUTH_CODE).append("=").append(code);
        return URLDecoder.decode(sb.toString(), "utf-8");
    }


}
