package com.home.small.sso.client.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Oauth2授权方式
 *
 * @author GTsung
 * @date 2021/10/30
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum GrantTypeEnum {

    /**
     * 授权码模式
     */
    AUTHORIZATION_CODE("authorization_code"),

    /**
     * 密码模式
     */
    PASSWORD("password"),
    ;

    @Getter
    private String value;

}
