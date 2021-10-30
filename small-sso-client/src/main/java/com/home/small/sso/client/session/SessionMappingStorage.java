package com.home.small.sso.client.session;

import javax.servlet.http.HttpSession;

/**
 * 存储token与session映射
 */
public interface SessionMappingStorage {

    /**
     * 通过token添加session
     * @param accessToken
     * @param session
     */
    void addSessionById(String accessToken, HttpSession session);

    /**
     * 通过sessionId删除session
     * @param sessionId
     */
    void removeBySessionId(String sessionId);

    /**
     * 通过token移除session
     * @param accessToken
     * @return
     */
    HttpSession removeSessionByMappingId(String accessToken);
}
