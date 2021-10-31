package com.home.small.sso.server.session;

import com.home.small.sso.server.common.AccessTokenContent;
import com.home.small.sso.server.common.Expiration;
import com.home.small.sso.server.common.RefreshTokenContent;

import java.util.UUID;

/**
 * 刷新凭证token管理
 */
public interface RefreshTokenManager extends Expiration {

    /**
     * 生成refreshToken
     * @param accessTokenContent
     * @param accessToken
     * @return
     */
    default  String generate(AccessTokenContent accessTokenContent, String accessToken) {
        String refreshToken = "RT-" + UUID.randomUUID().toString().replace("-", "");
        create(refreshToken, new RefreshTokenContent(accessTokenContent, accessToken));
        return refreshToken;
    }

    /**
     * 生成refreshToken
     * @param refreshToken
     * @param refreshTokenContent
     */
    void create(String refreshToken, RefreshTokenContent refreshTokenContent);

    /**
     * 验证refreshToken有效性，无论是否有效都移除
     * @param refreshToken
     * @return
     */
    RefreshTokenContent validate(String refreshToken);
}
