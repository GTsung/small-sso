package com.home.small.sso.client;

import com.home.small.sso.client.constants.SsoConstant;
import com.home.small.sso.client.filter.ClientFilter;
import com.home.small.sso.client.filter.ParamFilter;
import lombok.Setter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * filter容器
 *
 * @author GTsung
 * @date 2021/10/31
 */
public class FilterContainer extends ParamFilter implements Filter {

    /**
     * 不进行过滤的url
     */
    @Setter
    protected String excludeUrls;

    private ClientFilter[] filters;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (filters == null || filters.length == 0) {
            throw new IllegalArgumentException("filters不能为空");
        }
        for (ClientFilter filter : filters) {
            filter.setServerUrl(getServerUrl());
            filter.setAppId(getAppId());
            filter.setAppSecret(getAppSecret());
            filter.init(filterConfig);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 是否包含无需过滤的url
        if (isExcludeUrl(httpRequest.getServletPath())) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        for (ClientFilter filter : filters) {
            // 每个过滤器是否已经授权通过
            if (!filter.isAccessAllowed(httpRequest, httpResponse)) {
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isExcludeUrl(String reqUrl) {
        if (excludeUrls == null || excludeUrls.isEmpty()) {
            return false;
        }
        // 根据排除的url是否为模糊url分类
        Map<Boolean, List<String>> urlMap = Arrays.stream(excludeUrls.split(","))
                .collect(Collectors.partitioningBy(url -> url.endsWith(SsoConstant.URL_FUZZY_MATCH)));
        // 获取精确的url，优先精确匹配
        List<String> urlList = urlMap.get(false);
        if (urlList.contains(reqUrl)) {
            return true;
        }
        urlList = urlMap.get(true);
        for (String matchUrl: urlList) {
            // 模糊匹配，去除/*
            if (reqUrl.startsWith(matchUrl.replace(SsoConstant.URL_FUZZY_MATCH, ""))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
        if (filters == null || filters.length == 0) {
            return;
        }
        for (ClientFilter filter : filters) {
            filter.destroy();
        }
    }

    public void setFilters(ClientFilter... filters) {
        this.filters = filters;
    }
}
