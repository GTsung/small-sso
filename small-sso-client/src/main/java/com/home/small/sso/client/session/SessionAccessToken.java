package com.home.small.sso.client.session;

import com.home.small.sso.client.rpc.RpcAccessToken;
import com.home.small.sso.client.rpc.SsoUser;
import lombok.Getter;
import lombok.Setter;

/**
 * session中的token
 * @author GTsung
 * @date 2021/10/30
 */
public class SessionAccessToken  extends RpcAccessToken {

    private static final long serialVersionUID = 4507869346123296527L;

    /**
     * AccessToken的过期时间
     */
    @Getter
    @Setter
    private long expirationTime;

    public SessionAccessToken(String accessToken, int expiresIn, String refreshToken,
                              SsoUser user, long expirationTime) {
        super(accessToken, expiresIn, refreshToken, user);
        this.expirationTime = expirationTime;
    }

    /**
     * 是否已经过期
     * @return
     */
    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTime;
    }
}
