package com.home.small.sso.server.session.redis;

import com.alibaba.fastjson.JSON;
import com.home.small.sso.client.rpc.SsoUser;
import com.home.small.sso.server.session.TicketGrantingTicketManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 基于redis的TGT管理
 * @author GTsung
 * @date 2021/10/31
 */
@Component
@ConditionalOnProperty(name = "sso.session.manager", havingValue = "redis")
public class RedisTicketGrantingTicketManager implements TicketGrantingTicketManager {

    @Value("${sso.timeout}")
    private int timeout;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void create(String tgt, SsoUser user) {
        redisTemplate.opsForValue().set(tgt, JSON.toJSONString(user),
                getExpiresIn(), TimeUnit.SECONDS);
    }

    @Override
    public SsoUser getAndRefresh(String tgt) {
        String user = redisTemplate.opsForValue().get(tgt);
        if (StringUtils.isEmpty(user)) {
            return null;
        }
        redisTemplate.expire(tgt, timeout, TimeUnit.SECONDS);
        return JSON.parseObject(user, SsoUser.class);
    }

    @Override
    public void setUser(String tgt, SsoUser user) {
        create(tgt, user);
    }

    @Override
    public void removeTgt(String tgt) {
        redisTemplate.delete(tgt);
    }

    @Override
    public int getExpiresIn() {
        return timeout;
    }
}
