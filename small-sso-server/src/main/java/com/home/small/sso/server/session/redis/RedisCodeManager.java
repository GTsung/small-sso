package com.home.small.sso.server.session.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.home.small.sso.server.common.CodeContent;
import com.home.small.sso.server.session.CodeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 基于redis的授权码管理
 *
 * @author GTsung
 * @date 2021/10/31
 */
@Component
@ConditionalOnProperty(name = "sso.session.manager", havingValue = "redis")
public class RedisCodeManager implements CodeManager {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void create(String code, CodeContent codeContent) {
        redisTemplate.opsForValue().set(code, JSON.toJSONString(codeContent),
                getExpiresIn(), TimeUnit.SECONDS);
    }

    @Override
    public CodeContent getAndRemove(String code) {
        String codeContent = redisTemplate.opsForValue().get(code);
        if (!StringUtils.isEmpty(codeContent)) {
            redisTemplate.delete(code);
        }
        return JSONObject.parseObject(codeContent, CodeContent.class);
    }
}
