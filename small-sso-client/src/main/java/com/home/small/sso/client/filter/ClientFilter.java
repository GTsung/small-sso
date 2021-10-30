package com.home.small.sso.client.filter;

import com.home.small.sso.client.listener.LogoutListener;
import com.home.small.sso.client.session.SessionMappingStorage;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * filter基类
 *
 * @author GTsung
 * @date 2021/10/30
 */
public abstract class ClientFilter extends ParamFilter implements Filter {

    private SessionMappingStorage sessionMappingStorage;

    /**
     * 是否已经授权通过
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    public abstract boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response)
            throws IOException;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

    }

    @Override
    public void destroy() {

    }

    protected SessionMappingStorage getSessionMappingStorage() {
        if (Objects.isNull(sessionMappingStorage)) {
            sessionMappingStorage = LogoutListener.getSessionMappingStorage();
        }
        return sessionMappingStorage;
    }
}
