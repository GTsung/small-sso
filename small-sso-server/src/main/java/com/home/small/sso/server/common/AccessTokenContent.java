package com.home.small.sso.server.common;

import com.home.small.sso.client.rpc.SsoUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * token内容
 * @author GTsung
 * @date 2021/10/31
 */
@Getter
@Setter
@AllArgsConstructor
public class AccessTokenContent implements Serializable {

    private static final long serialVersionUID = 4587667812642196058L;

    /**
     * 授权存储信息
     */
    private CodeContent codeContent;

    /**
     * 用户
     */
    private SsoUser user;

    /**
     * 应用id
     */
    private String appId;
}
