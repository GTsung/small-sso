package com.home.small.sso.client.session.local;

import com.home.small.sso.client.session.SessionMappingStorage;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 维护session与token的映射关系
 *
 * @author GTsung
 * @date 2021/10/30
 */
public final class LocalSessionMappingStorage implements SessionMappingStorage {

    /**
     * key为sessionId，value为token
     */
    private final Map<String, String> sessionTokenMap = new HashMap<>();

    /**
     * key为token，value为session
     */
    private final Map<String, HttpSession> tokenSessionMap = new HashMap<>();

    @Override
    public synchronized void addSessionById(final String accessToken, final HttpSession session) {
        sessionTokenMap.put(session.getId(), accessToken);
        tokenSessionMap.put(accessToken, session);
    }

    @Override
    public synchronized void removeBySessionId(final String sessionId) {
        final String accessToken = sessionTokenMap.get(sessionId);
        sessionTokenMap.remove(sessionId);
        tokenSessionMap.remove(accessToken);
    }

    @Override
    public synchronized HttpSession removeSessionByMappingId(final String accessToken) {
        final HttpSession session = tokenSessionMap.get(accessToken);
        if (Objects.nonNull(session)) {
            removeBySessionId(session.getId());
        }
        return session;
    }
}
