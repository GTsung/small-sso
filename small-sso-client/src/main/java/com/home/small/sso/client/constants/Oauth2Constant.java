package com.home.small.sso.client.constants;

/**
 * @author GTsung
 * @date 2021/10/30
 */
public interface Oauth2Constant {

    /**
     * 用于保持请求和回调的状态，授权请求后原样带回给第三方，该参数可以用于防止csrf攻击
     */
    String STATE = "state";

    /**
     * 授权方式
     */
    String GRANT_TYPE = "grantType";

    /**
     * 应用唯一标志
     */
    String APP_ID = "appId";

    /**
     * 应用密钥
     */
    String APP_SECRET = "appSecret";

    /**
     * 刷新token
     */
    String REFRESH_TOKEN = "refreshToken";

    /**
     * 授权码模式的授权码
     */
    String AUTH_CODE = "code";

    /**
     * 密码模式的用户名
     */
    String USERNAME = "username";

    /**
     * 密码模式的密码
     */
    String PASSWORD = "password";

    /**
     * 获取accessToken的地址
     */
    String ACCESS_TOKEN_URL = "/oauth2/access_token";

    /**
     * 刷新accessToken的地址
     */
    String REFRESH_TOKEN_URL = "/oauth2/refresh_token";
}
