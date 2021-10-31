package com.home.small.sso.server.session.local;

import com.home.small.sso.client.rpc.SsoUser;
import com.home.small.sso.server.common.ExpirationPolicy;
import com.home.small.sso.server.session.TicketGrantingTicketManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地管理TGT
 *
 * @author GTsung
 * @date 2021/10/31
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "sso.session.manager", havingValue = "local")
public class LocalTicketGrantingTicketManager implements TicketGrantingTicketManager, ExpirationPolicy {

    /**
     * 有效期
     */
    @Value("${sso.timeout}")
    private int timeout;

    /**
     * key为生成的TGT，value为储存user与有效期的类
     */
    private Map<String, DummyTgt> tgtMap = new ConcurrentHashMap<>();

    @Override
    public void create(String tgt, SsoUser user) {
        // 将TGT与对应的user与有效期储存
        tgtMap.put(tgt, new DummyTgt(user, System.currentTimeMillis() + getExpiresIn() * 1000));
        log.info("登陆凭证生成成功，tgt: {}", tgt);
    }

    @Override
    public SsoUser getAndRefresh(String tgt) {
        DummyTgt dummyTgt = tgtMap.get(tgt);
        long currentTime = System.currentTimeMillis();
        if (dummyTgt == null) {
            return null;
        }
        // 刷新过期时间
        dummyTgt.expired = currentTime + getExpiresIn() * 1000;
        return dummyTgt.ssoUser;
    }

    @Override
    public void setUser(String tgt, SsoUser user) {
        DummyTgt dummyTgt = tgtMap.get(tgt);
        if (dummyTgt == null) {
            return;
        }
        // 更新用户信息
        dummyTgt.ssoUser = user;
    }

    @Override
    public void removeTgt(String tgt) {
        tgtMap.remove(tgt);
        log.info("登陆凭证删除成功，tgt: {}", tgt);
    }

    @Scheduled(cron = SCHEDULED_CRON)
    @Override
    public void verifyExpired() {
        tgtMap.forEach((tgt, dummyTgt) -> {
            if (System.currentTimeMillis() > dummyTgt.expired) {
                tgtMap.remove(tgt);
                log.info("登陆凭证已失效，tgt: {}", tgt);
            }
        });
    }

    @Override
    public int getExpiresIn() {
        return timeout;
    }

    /**
     * 储存user的类
     */
    private class DummyTgt {
        private SsoUser ssoUser;
        private long expired;

        public DummyTgt(SsoUser ssoUser, long expired) {
            super();
            this.ssoUser = ssoUser;
            this.expired = expired;
        }
    }
}
