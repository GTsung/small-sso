package com.home.small.sso.server.session.local;

import com.home.small.sso.server.common.CodeContent;
import com.home.small.sso.server.common.ExpirationPolicy;
import com.home.small.sso.server.session.CodeManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author GTsung
 * @date 2021/10/31
 */
@Slf4j
@Component
public class LocalCodeManager implements CodeManager, ExpirationPolicy {

    private Map<String, DummyCode> codeMap = new ConcurrentHashMap<>();

    @Override
    public void create(String code, CodeContent codeContent) {
        codeMap.put(code, new DummyCode(codeContent, System.currentTimeMillis() + getExpiresIn() * 1000));
        log.info("授权码生成成功，code: {}", code);
    }

    @Override
    public CodeContent getAndRemove(String code) {
        DummyCode dc = codeMap.remove(code);
        if (dc == null || System.currentTimeMillis() > dc.expired) {
            return null;
        }
        return dc.codeContent;
    }

    @Scheduled(cron = SCHEDULED_CRON)
    @Override
    public void verifyExpired() {
        codeMap.forEach((code, dummyCode) -> {
            if (System.currentTimeMillis() > dummyCode.expired) {
                codeMap.remove(code);
                log.info("授权码已经失效， code: {}", code);
            }
        });
    }

    private class DummyCode {
        private CodeContent codeContent;
        // 过期时间
        private long expired;

        public DummyCode(CodeContent codeContent, long expired) {
            super();
            this.codeContent = codeContent;
            this.expired = expired;
        }
    }
}
