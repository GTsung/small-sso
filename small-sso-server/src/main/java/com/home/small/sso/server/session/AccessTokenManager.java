package com.home.small.sso.server.session;

import com.home.small.sso.client.constants.SsoConstant;
import com.home.small.sso.client.utils.HttpUtils;
import com.home.small.sso.server.common.AccessTokenContent;
import com.home.small.sso.server.common.Expiration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * AccessToken管理
 */
public interface AccessTokenManager extends Expiration {

    /**
     * 生成token
     *
     * @param accessTokenContent
     * @return
     */
    default String generate(AccessTokenContent accessTokenContent) {
        String accessToken = "AT-" + UUID.randomUUID().toString().replace("-", "");
        create(accessToken, accessTokenContent);
        return accessToken;
    }

    /**
     * 生成token
     *
     * @param accessToken
     * @param accessTokenContent
     */
    void create(String accessToken, AccessTokenContent accessTokenContent);

    /**
     * 延长AccessToken生命周期
     *
     * @param accessToken
     * @return
     */
    boolean refresh(String accessToken);

    /**
     * 查询
     *
     * @param accessToken
     * @return
     */
    AccessTokenContent get(String accessToken);

    /**
     * 根据TGT删除AccessToken
     *
     * @param tgt
     */
    void removeByTgt(String tgt);

    /**
     * 发起客户端登出请求
     * @param redirectUri
     * @param accessToken
     */
    default void sendLogoutRequest(String redirectUri, String accessToken) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(SsoConstant.LOGOUT_PARAMETER_NAME, accessToken);
        HttpUtils.postHeader(redirectUri, headerMap);
    }

}
