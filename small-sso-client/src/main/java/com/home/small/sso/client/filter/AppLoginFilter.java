package com.home.small.sso.client.filter;

import com.home.small.sso.client.constants.SsoConstant;
import com.home.small.sso.client.session.SessionAccessToken;
import com.home.small.sso.client.utils.SessionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * APP单点登录filter
 *
 * @author GTsung
 * @date 2021/10/31
 */
public class AppLoginFilter extends LoginFilter {

    @Override
    public boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionAccessToken sessionAccessToken = SessionUtils.getAccessToken(request);
        // 本地session中存在token且(token没有过期或者刷新token成功)，不再拦截
        if (sessionAccessToken != null && (!sessionAccessToken.isExpired()
                || refreshToken(sessionAccessToken.getRefreshToken(), request))) {
            return true;
        }
        responseJson(response, SsoConstant.NO_LOGIN, "未登录或已超时");
        return false;
    }
}
