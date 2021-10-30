package com.home.small.sso.client.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.home.small.sso.client.constants.Oauth2Constant;
import com.home.small.sso.client.enums.GrantTypeEnum;
import com.home.small.sso.client.rpc.Result;
import com.home.small.sso.client.rpc.RpcAccessToken;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GTsung
 * @date 2021/10/30
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Oauth2Utils {


    /**
     * 从认证中心获取accessToken，采用oauth2的密码模式
     *
     * @param serverUrl
     * @param appId
     * @param appSecret
     * @param username
     * @param password
     * @return
     */
    public static Result<RpcAccessToken> getAccessToken(String serverUrl, String appId, String appSecret,
                                                        String username, String password) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(Oauth2Constant.GRANT_TYPE, GrantTypeEnum.PASSWORD.getValue());
        paramMap.put(Oauth2Constant.APP_ID, appId);
        paramMap.put(Oauth2Constant.APP_SECRET, appSecret);
        paramMap.put(Oauth2Constant.USERNAME, username);
        paramMap.put(Oauth2Constant.PASSWORD, password);
        return getHttpAccessToken(serverUrl + Oauth2Constant.ACCESS_TOKEN_URL, paramMap);
    }

    /**
     * oauth2的授权码模式获取accessToken
     *
     * @param serverUrl
     * @param appId
     * @param appSecret
     * @param code
     * @return
     */
    public static Result<RpcAccessToken> getAccessToken(String serverUrl, String appId,
                                                        String appSecret, String code) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(Oauth2Constant.GRANT_TYPE, GrantTypeEnum.AUTHORIZATION_CODE.getValue());
        paramMap.put(Oauth2Constant.APP_ID, appId);
        paramMap.put(Oauth2Constant.APP_SECRET, appSecret);
        paramMap.put(Oauth2Constant.AUTH_CODE, code);
        return getHttpAccessToken(serverUrl + Oauth2Constant.ACCESS_TOKEN_URL, paramMap);
    }

    /**
     * 刷新token
     * @param serverUrl
     * @param appId
     * @param refreshToken
     * @return
     */
    public static Result<RpcAccessToken> refreshToken(String serverUrl, String appId, String refreshToken) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(Oauth2Constant.APP_ID, appId);
        paramMap.put(Oauth2Constant.REFRESH_TOKEN, refreshToken);
        return getHttpAccessToken(serverUrl + Oauth2Constant.REFRESH_TOKEN_URL, paramMap);
    }

    private static Result<RpcAccessToken> getHttpAccessToken(String url, Map<String, String> paramMap) {
        String jsonStr = HttpUtils.get(url, paramMap);
        if (jsonStr == null || jsonStr.isEmpty()) {
            log.info("get HttpAccessToken exception, return null, url: {}", url);
            return null;
        }
        return JSONObject.parseObject(jsonStr, new TypeReference<Result<RpcAccessToken>>() {
        });
    }

}
