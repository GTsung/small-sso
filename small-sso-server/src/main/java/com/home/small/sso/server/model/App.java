package com.home.small.sso.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 应用
 * @author GTsung
 * @date 2021/10/31
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class App implements Serializable {

    private static final long serialVersionUID = 14358427303197385L;

    /**
     * 应用名称
     */
    private String name;

    /**
     * appId
     */
    private String appId;

    /**
     * app密钥
     */
    private String appSecret;
}
