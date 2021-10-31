package com.home.small.sso.server.session;

import com.home.small.sso.client.rpc.SsoUser;
import com.home.small.sso.server.common.Expiration;

import java.util.UUID;

/**
 * 登陆凭证(TGT)管理
 */
public interface TicketGrantingTicketManager extends Expiration {


    /**
     * 登陆成功后生成登陆凭证tgt
     * @param user
     * @return
     */
    default String generate(SsoUser user) {
        String tgt = "TGT-" + UUID.randomUUID().toString().replace("-", "");
        create(tgt, user);
        return tgt;
    }

    /**
     * 登陆成功后根据用户信息生成令牌
     * @param tgt
     * @param user
     */
    void create(String tgt, SsoUser user);

    /**
     * 验证tgt是否存在且是否有效，并更新过期时间
     * @param tgt
     * @return
     */
    SsoUser getAndRefresh(String tgt);

    /**
     * 根据tgt设置新的用户信息
     * @param tgt
     * @param user
     */
    void setUser(String tgt, SsoUser user);

    /**
     * 移除tgt
     * @param tgt
     */
    void removeTgt(String tgt);
}
