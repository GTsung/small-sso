package com.home.small.sso.client.constants;

public interface SsoConstant {

    /**
     * 服务端登录地址
     */
    String LOGIN_URL = "/login";

    /**
     * 服务端登出地址
     */
    String LOGOUT_URL = "/logout";

    /**
     * 服务端回调客户端地址参数名称
     */
    String REDIRECT_URI = "redirectUri";

    /**
     * 服务端单点登出回调客户端登出参数名称
     */
    String LOGOUT_PARAMETER_NAME = "logoutRequest";

    /**
     * 本地session中的AccessToken信息
     */
    String SESSION_ACCESS_TOKEN = "_sessionAccessToken";

    /**
     * 模糊匹配后缀
     */
    String URL_FUZZY_MATCH = "/*";

    /**
     * 未登录或已过期
     */
    int NO_LOGIN = 2100;
}
