package com.home.small.sso.server.session.local;

import com.home.small.sso.server.common.ExpirationPolicy;
import com.home.small.sso.server.common.RefreshTokenContent;
import com.home.small.sso.server.session.RefreshTokenManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ConditionalOnProperty(name = "sso.session.manager", havingValue = "local")
public class LocalRefreshTokenManager implements RefreshTokenManager, ExpirationPolicy {

    @Value("${sso.timeout}")
    private int timeout;

    private Map<String, DummyRefreshToken> refreshTokenMap = new ConcurrentHashMap<>();

    @Override
    public void create(String refreshToken, RefreshTokenContent refreshTokenContent) {
        DummyRefreshToken dummyRefreshToken = new DummyRefreshToken(refreshTokenContent, System.currentTimeMillis() + getExpiresIn() * 1000);
        refreshTokenMap.put(refreshToken, dummyRefreshToken);
    }

    @Override
    public RefreshTokenContent validate(String refreshToken) {
        DummyRefreshToken dummyRefreshToken = refreshTokenMap.remove(refreshToken);
        if (dummyRefreshToken == null || System.currentTimeMillis() > dummyRefreshToken.expired) {
            return null;
        }
        return dummyRefreshToken.refreshTokenContent;
    }

    @Scheduled(cron = SCHEDULED_CRON)
    @Override
    public void verifyExpired() {
        refreshTokenMap.forEach((refreshToken, dummyRefreshToken) -> {
            if (System.currentTimeMillis() > dummyRefreshToken.expired) {
                refreshTokenMap.remove(refreshToken);
                log.info("refreshToken: {}, 已经失效", refreshToken);
            }
        });
    }

    @Override
    public int getExpiresIn() {
        // 和TGT时效一致
        return timeout;
    }

    private class DummyRefreshToken {
        private RefreshTokenContent refreshTokenContent;
        // 过期时间
        private long expired;

        public DummyRefreshToken(RefreshTokenContent refreshTokenContent, long expired) {
            super();
            this.refreshTokenContent = refreshTokenContent;
            this.expired = expired;
        }
    }
}
