package com.home.small.sso.client.filter;

import lombok.Getter;
import lombok.Setter;

/**
 * 参数注入
 *
 * @author GTsung
 * @date 2021/10/30
 */
@Getter
@Setter
public class ParamFilter {

    /**
     * 应用id
     */
    private String appId;

    /**
     * 应用密钥
     */
    private String appSecret;

    /**
     * sso服务器地址
     */
    private String serverUrl;
}
