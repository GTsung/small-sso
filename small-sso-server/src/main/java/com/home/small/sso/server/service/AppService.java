package com.home.small.sso.server.service;

import com.home.small.sso.client.rpc.Result;

public interface AppService {

    /**
     * 是否存在appId
     * @param appId
     * @return
     */
    boolean exists(String appId);

    /**
     * 验证appId与appSecret
     * @param appId
     * @param appSecret
     * @return
     */
    Result<Void> validate(String appId, String appSecret);
}
