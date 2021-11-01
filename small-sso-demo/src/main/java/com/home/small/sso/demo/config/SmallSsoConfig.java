package com.home.small.sso.demo.config;

import com.home.small.sso.client.FilterContainer;
import com.home.small.sso.client.filter.AppLoginFilter;
import com.home.small.sso.client.filter.AppLogoutFilter;
import com.home.small.sso.client.filter.LoginFilter;
import com.home.small.sso.client.filter.LogoutFilter;
import com.home.small.sso.client.listener.LogoutListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpSessionListener;

/**
 * @author GTsung
 * @date 2021/10/31
 */
@Configuration
public class SmallSsoConfig {

    @Value("${sso.server.url}")
    private String serverUrl;
    @Value("${sso.app.id}")
    private String appId;
    @Value("${sso.app.secret}")
    private String appSecret;

    /**
     * 单实例方式单点登出Listener
     *
     * @return
     */
    @Bean
    public ServletListenerRegistrationBean<HttpSessionListener> logoutListener() {
        ServletListenerRegistrationBean<HttpSessionListener> listenerRegBean = new ServletListenerRegistrationBean<>();
        LogoutListener logoutListener = new LogoutListener();
        listenerRegBean.setListener(logoutListener);
        return listenerRegBean;
    }

    /**
     * 分布式redis方式注册单点登出Listener
     *
     * 注：
     * 1.需注入RedisSessionMappingStorage
     * 2.需要使用Spring方式注入LogoutListener，使用ServletListenerRegistrationBean方式不生效
     */
//	@Autowired
//	private SessionMappingStorage sessionMappingStorage;
//
//	@Bean
//	public SessionMappingStorage sessionMappingStorage() {
//		return new RedisSessionMappingStorage();
//	}
//
//	@Bean
//	public ApplicationListener<AbstractSessionEvent> LogoutListener() {
//		List<HttpSessionListener> httpSessionListeners = new ArrayList<>();
//		LogoutListener logoutListener = new LogoutListener();
//		logoutListener.setSessionMappingStorage(sessionMappingStorage);
//		httpSessionListeners.add(logoutListener);
//		return new SessionEventHttpSessionListenerAdapter(httpSessionListeners);
//	}

    /**
     * Web支持单点登录Filter容器
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean<FilterContainer> smartContainer() {
        FilterContainer filterContainer = new FilterContainer();
        filterContainer.setServerUrl(serverUrl);
        filterContainer.setAppId(appId);
        filterContainer.setAppSecret(appSecret);

        filterContainer.setFilters(new LogoutFilter(), new LoginFilter());

        FilterRegistrationBean<FilterContainer> registration = new FilterRegistrationBean<>();
        registration.setFilter(filterContainer);
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        registration.setName("filterContainer");
        return registration;
    }

    /**
     * App登录Filter容器（可与Web方式共存）
     *
     * @return
     */
	@Bean
	public FilterRegistrationBean<FilterContainer> appSmartContainer() {
        FilterContainer filterContainer = new FilterContainer();
        filterContainer.setServerUrl(serverUrl);
        filterContainer.setAppId(appId);
        filterContainer.setAppSecret(appSecret);

        filterContainer.setExcludeUrls("/app/login");

        filterContainer.setFilters(new AppLogoutFilter(), new AppLoginFilter());

		FilterRegistrationBean<FilterContainer> registration = new FilterRegistrationBean<>();
		registration.setFilter(filterContainer);
		registration.addUrlPatterns("/app/*");
		registration.setOrder(2);
		registration.setName("appFilterContainer");
		return registration;
	}
}
