package com.home.small.sso.client.listener;

import com.home.small.sso.client.session.SessionMappingStorage;
import com.home.small.sso.client.session.local.LocalSessionMappingStorage;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * 单点登出Listener
 * <p>
 * 用于本地session过期，删除accessToken与session的映射关系
 *
 * @author GTsung
 * @date 2021/10/30
 */
public final class LogoutListener implements HttpSessionListener {

    private static SessionMappingStorage sessionMappingStorage = new LocalSessionMappingStorage();

    @Override
    public void sessionCreated(final HttpSessionEvent event) {

    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent event) {
        final HttpSession session = event.getSession();
        sessionMappingStorage.removeBySessionId(session.getId());
    }

    public void setSessionMappingStorage(SessionMappingStorage sms) {
        sessionMappingStorage = sms;
    }

    public static SessionMappingStorage getSessionMappingStorage() {
        return sessionMappingStorage;
    }
}
