package com.home.small.sso.server.session.local;

import com.home.small.sso.server.common.AccessTokenContent;
import com.home.small.sso.server.common.CodeContent;
import com.home.small.sso.server.common.ExpirationPolicy;
import com.home.small.sso.server.session.AccessTokenManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author GTsung
 * @date 2021/10/31
 */
@Slf4j
@Component
public class LocalAccessTokenManager implements AccessTokenManager, ExpirationPolicy {

    @Value("${sso.timeout}")
    private int timeout;

    /**
     * key为accessToken，value为储存accessToken的对象
     */
    private Map<String, DummyAccessToken> accessTokenMap = new ConcurrentHashMap<>();

    /**
     * key为TGT，value为该登陆凭证下的所有accessToken集合
     */
    private Map<String, Set<String>> tgtMap = new ConcurrentHashMap<>();

    @Override
    public void create(String accessToken, AccessTokenContent accessTokenContent) {
        DummyAccessToken dummyAccessToken = new DummyAccessToken(accessTokenContent, System.currentTimeMillis() + getExpiresIn() * 1000);
        // 放入到缓存中
        accessTokenMap.put(accessToken, dummyAccessToken);
        // 将此登陆凭证TGT下的accessToken放入到集合中
        tgtMap.computeIfAbsent(accessTokenContent.getCodeContent().getTgt(), a -> new HashSet<>()).add(accessToken);
        log.info("调用凭证生成accessToken成功，accessToken: {}", accessToken);
    }

    @Override
    public boolean refresh(String accessToken) {
        DummyAccessToken dummyAccessToken = accessTokenMap.get(accessToken);
        if (dummyAccessToken == null || System.currentTimeMillis() > dummyAccessToken.expired) {
            return false;
        }
        dummyAccessToken.expired = System.currentTimeMillis() + getExpiresIn() * 1000;
        return true;
    }

    @Override
    public AccessTokenContent get(String accessToken) {
        DummyAccessToken dummyAccessToken = accessTokenMap.get(accessToken);
        if (dummyAccessToken == null || System.currentTimeMillis() > dummyAccessToken.expired) {
            return null;
        }
        return dummyAccessToken.accessTokenContent;
    }

    @Override
    public void removeByTgt(String tgt) {
        Set<String> accessTokenSet = tgtMap.get(tgt);
        if (CollectionUtils.isEmpty(accessTokenSet)) {
            return;
        }
        accessTokenSet.forEach(accessToken -> {
            DummyAccessToken dummyAccessToken = accessTokenMap.get(accessToken);
            if (dummyAccessToken == null || System.currentTimeMillis() > dummyAccessToken.expired) {
                return;
            }
            CodeContent codeContent = dummyAccessToken.accessTokenContent.getCodeContent();
            // 如果授权码内容不是登出
            if (codeContent == null || !codeContent.isSendLogoutRequest()) {
                return;
            }
            // 发送登出请求
            log.info("发起客户端登出请求，accessToken: {}, url: {}", accessToken, codeContent.getRedirectUri());
            sendLogoutRequest(codeContent.getRedirectUri(), accessToken);
        });
    }

    @Scheduled(cron = SCHEDULED_CRON)
    @Override
    public void verifyExpired() {
        accessTokenMap.forEach((accessToken, dummyAccessToken) -> {
            if (System.currentTimeMillis() > dummyAccessToken.expired) {
                accessTokenMap.remove(accessToken);
                log.info("调用凭证token已经失效, accessToken: {}", accessToken);
            }
        });
    }

    @Override
    public int getExpiresIn() {
        // token时效为TGT时效的一半
        return timeout / 2;
    }

    private class DummyAccessToken {
        private AccessTokenContent accessTokenContent;
        // 过期时间
        private long expired;

        public DummyAccessToken(AccessTokenContent accessTokenContent, long expired) {
            super();
            this.accessTokenContent = accessTokenContent;
            this.expired = expired;
        }
    }
}
