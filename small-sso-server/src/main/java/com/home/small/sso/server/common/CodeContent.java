package com.home.small.sso.server.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 授权存储信息
 * @author GTsung
 * @date 2021/10/31
 */
@Getter
@Setter
@AllArgsConstructor
public class CodeContent implements Serializable {

    private static final long serialVersionUID = -1332598459045608781L;

    /**
     * TicketGrantingTicket
     */
    private String tgt;

    /**
     * 登出
     */
    private boolean sendLogoutRequest;

    /**
     * 跳转
     */
    private String redirectUri;
}
