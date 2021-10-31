package com.home.small.sso.server.session.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.home.small.sso.server.common.RefreshTokenContent;
import com.home.small.sso.server.session.RefreshTokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author GTsung
 * @date 2021/10/31
 */
@Component
@ConditionalOnProperty(name = "sso.session.manager", havingValue = "redis")
public class RedisRefreshTokenManager implements RefreshTokenManager {

    @Value("${sso.timeout}")
    private int timeout;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void create(String refreshToken, RefreshTokenContent refreshTokenContent) {
        redisTemplate.opsForValue().set(refreshToken, JSON.toJSONString(refreshTokenContent),
                getExpiresIn(), TimeUnit.SECONDS);
    }

    @Override
    public RefreshTokenContent validate(String refreshToken) {
        String tokenContent = redisTemplate.opsForValue().get(refreshToken);
        if (!StringUtils.isEmpty(tokenContent)) {
            redisTemplate.delete(refreshToken);
        }
        return JSONObject.parseObject(tokenContent, RefreshTokenContent.class);
    }

    @Override
    public int getExpiresIn() {
        return timeout;
    }
}
