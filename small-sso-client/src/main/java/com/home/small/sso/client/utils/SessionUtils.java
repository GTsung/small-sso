package com.home.small.sso.client.utils;

import com.home.small.sso.client.constants.SsoConstant;
import com.home.small.sso.client.rpc.RpcAccessToken;
import com.home.small.sso.client.rpc.SsoUser;
import com.home.small.sso.client.session.SessionAccessToken;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @author GTsung
 * @date 2021/10/30
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SessionUtils {

    /**
     * 从session中获取token
     * @param request
     * @return
     */
    public static SessionAccessToken getAccessToken(HttpServletRequest request) {
        return (SessionAccessToken) request.getSession().getAttribute(SsoConstant.SESSION_ACCESS_TOKEN);
    }

    public static SsoUser getUser(HttpServletRequest request) {
        return Optional.ofNullable(getAccessToken(request)).map(RpcAccessToken::getUser).orElse(null);
    }

    public static Integer getUserId(HttpServletRequest request) {
        return Optional.ofNullable(getUser(request)).map(SsoUser::getId).orElse(null);
    }

    /**
     * 将从认证中心获取的token放入到session
     * @param request
     * @param rpcAccessToken
     */
    public static void setAccessToken(HttpServletRequest request, RpcAccessToken rpcAccessToken) {
        SessionAccessToken sessionAccessToken = null;
        if (rpcAccessToken != null) {
            sessionAccessToken = createSessionAccessToken(rpcAccessToken);
        }
        request.getSession().setAttribute(SsoConstant.SESSION_ACCESS_TOKEN, sessionAccessToken);
    }

    private static SessionAccessToken createSessionAccessToken(RpcAccessToken rpcAccessToken) {
        long expirationTime = System.currentTimeMillis() + rpcAccessToken.getExpiresIn() * 1000;
        return new SessionAccessToken(rpcAccessToken.getAccessToken(), rpcAccessToken.getExpiresIn(),
                rpcAccessToken.getRefreshToken(), rpcAccessToken.getUser(), expirationTime);
    }

    /**
     * 销毁session
     * @param request
     */
    public static void invalidate(HttpServletRequest request) {
        setAccessToken(request, null);
        request.getSession().invalidate();
    }
}
