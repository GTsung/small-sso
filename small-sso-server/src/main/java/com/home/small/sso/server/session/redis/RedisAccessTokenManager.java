package com.home.small.sso.server.session.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.home.small.sso.server.common.AccessTokenContent;
import com.home.small.sso.server.session.AccessTokenManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author GTsung
 * @date 2021/10/31
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "sso.session.manager", havingValue = "redis")
public class RedisAccessTokenManager implements AccessTokenManager {

    @Value("${sso.timeout}")
    private int timeout;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void create(String accessToken, AccessTokenContent accessTokenContent) {
        // 缓存accessToken与tokenContent对应关系
        redisTemplate.opsForValue().set(accessToken, JSON.toJSONString(accessTokenContent),
                getExpiresIn(), TimeUnit.SECONDS);
        // 缓存tgt与多个accessToken的对应关系
        redisTemplate.opsForSet().add(getKey(accessTokenContent.getCodeContent().getTgt()), accessToken);
    }

    private String getKey(String tgt) {
        return tgt + "_access_token";
    }

    @Override
    public boolean refresh(String accessToken) {
        if (redisTemplate.opsForValue().get(accessToken) == null) {
            return false;
        }
        redisTemplate.expire(accessToken, timeout, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public AccessTokenContent get(String accessToken) {
        String atcStr = redisTemplate.opsForValue().get(accessToken);
        if (StringUtils.isEmpty(atcStr)) {
            return null;
        }
        return JSONObject.parseObject(atcStr, AccessTokenContent.class);
    }

    @Override
    public void removeByTgt(String tgt) {
        Set<String> accessTokenSet = redisTemplate.opsForSet().members(getKey(tgt));
        if (CollectionUtils.isEmpty(accessTokenSet)) {
            return;
        }
        // 先删除tgt与多个token的关系
        redisTemplate.delete(getKey(tgt));

        accessTokenSet.forEach(accessToken -> {
            String atcStr = redisTemplate.opsForValue().get(accessToken);
            if (StringUtils.isEmpty(atcStr)) {
                return;
            }
            AccessTokenContent accessTokenContent = JSONObject.parseObject(atcStr, AccessTokenContent.class);
            // 判断是否需要发送登出请求
            if (accessTokenContent == null || !accessTokenContent.getCodeContent().isSendLogoutRequest()) {
                return;
            }
            // 登出
            log.info("发起客户端登出请求，accessToken: {}, url: {}", accessToken, accessTokenContent.getCodeContent().getRedirectUri());
            sendLogoutRequest(accessTokenContent.getCodeContent().getRedirectUri(), accessToken);
        });
    }

    @Override
    public int getExpiresIn() {
        return timeout / 2;
    }
}
