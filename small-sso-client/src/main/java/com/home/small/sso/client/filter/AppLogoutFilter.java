package com.home.small.sso.client.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author GTsung
 * @date 2021/10/31
 */
public class AppLogoutFilter extends LogoutFilter {

    @Override
    public boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (getLogoutParam(request) != null) {
            request.getSession().invalidate();
            return false;
        }
        return true;
    }
}
