package com.home.small.sso.client.rpc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 服务端回传的token对象
 *
 * @author GTsung
 * @date 2021/10/30
 */
@Getter
@Setter
@AllArgsConstructor
public class RpcAccessToken implements Serializable {

    private static final long serialVersionUID = 1764365572111947234L;

    /**
     * 调用凭证
     */
    private String accessToken;

    /**
     * AccessToken超时时间，单位秒
     */
    private int expiresIn;

    /**
     * 当前token超时，用于刷新token并延长服务端session时效的必要参数
     */
    private String refreshToken;

    /**
     * 用户信息
     */
    private SsoUser user;
}
