package com.home.small.sso.client.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * filter基类
 * @author GTsung
 * @date 2021/10/30
 */
public class ClientFilter extends ParamFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

    }

    @Override
    public void destroy() {

    }
}
