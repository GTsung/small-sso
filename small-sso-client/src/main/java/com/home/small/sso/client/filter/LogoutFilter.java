package com.home.small.sso.client.filter;

import com.home.small.sso.client.constants.SsoConstant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 单点登出filter
 *
 * @author GTsung
 * @date 2021/10/30
 */
public class LogoutFilter extends ClientFilter {

    @Override
    public boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accessToken = getLogoutParam(request);
        if (accessToken != null) {
            // 销毁session，登出系统
            destroySession(accessToken);
            return false;
        }
        return true;
    }

    protected String getLogoutParam(HttpServletRequest request) {
        return request.getHeader(SsoConstant.LOGOUT_PARAMETER_NAME);
    }

    private void destroySession(String accessToken) {
        // 将session与token映射关系删除
        final HttpSession session = getSessionMappingStorage().removeSessionByMappingId(accessToken);
        if (session != null) {
            // 销毁session
            session.invalidate();
        }
    }

}
