package com.home.small.sso.server.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 刷新令牌
 * @author GTsung
 * @date 2021/10/31
 */
@Getter
@Setter
@AllArgsConstructor
public class RefreshTokenContent implements Serializable {

    private static final long serialVersionUID = -1332598459045608781L;

    /**
     * 存储信息
     */
    private AccessTokenContent accessTokenContent;

    private String accessToken;
}
