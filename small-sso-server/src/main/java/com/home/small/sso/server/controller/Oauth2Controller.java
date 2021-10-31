package com.home.small.sso.server.controller;

import com.home.small.sso.client.constants.Oauth2Constant;
import com.home.small.sso.client.enums.GrantTypeEnum;
import com.home.small.sso.client.rpc.Result;
import com.home.small.sso.client.rpc.RpcAccessToken;
import com.home.small.sso.client.rpc.SsoUser;
import com.home.small.sso.server.common.AccessTokenContent;
import com.home.small.sso.server.common.CodeContent;
import com.home.small.sso.server.common.RefreshTokenContent;
import com.home.small.sso.server.service.AppService;
import com.home.small.sso.server.service.UserService;
import com.home.small.sso.server.session.AccessTokenManager;
import com.home.small.sso.server.session.CodeManager;
import com.home.small.sso.server.session.RefreshTokenManager;
import com.home.small.sso.server.session.TicketGrantingTicketManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author GTsung
 * @date 2021/10/31
 */
@RestController
@RequestMapping("/oauth2")
public class Oauth2Controller {

    @Autowired
    private AppService appService;
    @Autowired
    private UserService userService;
    @Autowired
    private CodeManager codeManager;
    @Autowired
    private AccessTokenManager accessTokenManager;
    @Autowired
    private RefreshTokenManager refreshTokenManager;
    @Autowired
    private TicketGrantingTicketManager ticketGrantingTicketManager;

    /**
     * 客户端获取token
     *
     * @param grantType
     * @param appId
     * @param appSecret
     * @param code
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value = "/access_token", method = RequestMethod.GET)
    public Result getAccessToken(
            @RequestParam(value = Oauth2Constant.GRANT_TYPE) String grantType,
            @RequestParam(value = Oauth2Constant.APP_ID) String appId,
            @RequestParam(value = Oauth2Constant.APP_SECRET) String appSecret,
            @RequestParam(value = Oauth2Constant.AUTH_CODE, required = false) String code,
            @RequestParam(value = Oauth2Constant.USERNAME, required = false) String username,
            @RequestParam(value = Oauth2Constant.PASSWORD, required = false) String password) {
        // 校验参数
        Result<Void> result = validateParam(grantType, code, username, password);
        if (!result.isSuccess()) {
            return result;
        }

        // 校验应用
        Result<Void> appResult = appService.validate(appId, appSecret);
        if (!appResult.isSuccess()) {
            return appResult;
        }

        // 校验授权
        Result<AccessTokenContent> accessTokenContentResult = validateAuth(grantType, code, username, password, appId);
        if (!accessTokenContentResult.isSuccess()) {
            return accessTokenContentResult;
        }

        // 生成token返回
        return Result.createSuccess(generateRpcAccessToken(accessTokenContentResult.getData(), null));
    }

    /**
     * 校验基本参数
     *
     * @param grantType
     * @param code
     * @param username
     * @param password
     * @return
     */
    private Result<Void> validateParam(String grantType, String code, String username, String password) {
        if (GrantTypeEnum.AUTHORIZATION_CODE.getValue().equals(grantType)) {
            if (StringUtils.isEmpty(code)) {
                return Result.createError("code不能为空");
            }
        } else if (GrantTypeEnum.PASSWORD.getValue().equals(grantType)) {
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                return Result.createError("username和password不能为空");
            }
        } else {
            return Result.createError("授权方式不对");
        }
        return Result.createSuccess();
    }

    /**
     * 校验授权
     *
     * @param grantType
     * @param code
     * @param username
     * @param password
     * @return
     */
    private Result<AccessTokenContent> validateAuth(String grantType, String code, String username,
                                                    String password, String appId) {
        AccessTokenContent accessTokenContent = null;
        if (GrantTypeEnum.AUTHORIZATION_CODE.getValue().equals(grantType)) {
            // 通过参数获取code内容
            CodeContent codeContent = codeManager.getAndRemove(code);
            if (codeContent == null) {
                return Result.createError("授权码code有误或者已经过期");
            }
            // 通过TGT获取user信息
            SsoUser user = ticketGrantingTicketManager.getAndRefresh(codeContent.getTgt());
            if (user == null) {
                return Result.createError("服务端session已经过期");
            }
            accessTokenContent = new AccessTokenContent(codeContent, user, appId);
        } else if (GrantTypeEnum.PASSWORD.getValue().equals(grantType)) {
            // app通过此方式由客户端代理转发http请求到服务端获取token
            Result<SsoUser> loginResult = userService.login(username, password);
            if (!loginResult.isSuccess()) {
                return Result.createError(loginResult.getMessage());
            }
            SsoUser user = loginResult.getData();
            // 生成TGT
            String tgt = ticketGrantingTicketManager.generate(user);
            CodeContent codeContent = new CodeContent(tgt, false, null);
            accessTokenContent = new AccessTokenContent(codeContent, user, appId);
        }
        return Result.createSuccess(accessTokenContent);
    }

    /**
     * 生成accessToken
     * @param accessTokenContent
     * @param accessToken
     * @return
     */
    private RpcAccessToken generateRpcAccessToken(AccessTokenContent accessTokenContent, String accessToken) {
        String newAccessToken = accessToken;
        // token为空或者失效，生成Token
        if (newAccessToken == null || !accessTokenManager.refresh(newAccessToken)) {
            newAccessToken = accessTokenManager.generate(accessTokenContent);
        }
        // 生成用来刷新的refreshToken
        String refreshToken = refreshTokenManager.generate(accessTokenContent, newAccessToken);
        return new RpcAccessToken(newAccessToken, accessTokenManager.getExpiresIn(), refreshToken,
                accessTokenContent.getUser());
    }

    @RequestMapping(value = "/refresh_token", method = RequestMethod.GET)
    public Result refreshToken(@RequestParam(value = Oauth2Constant.APP_ID) String appId,
                               @RequestParam(value = Oauth2Constant.REFRESH_TOKEN) String refreshToken) {
        if (!appService.exists(appId)) {
            return Result.createError("非法应用");
        }
        // 检查refreshToken是否已经过期
        RefreshTokenContent refreshTokenContent = refreshTokenManager.validate(refreshToken);
        if (refreshTokenContent == null) {
            return Result.createError("refreshToken有误或已过期");
        }

        AccessTokenContent accessTokenContent = refreshTokenContent.getAccessTokenContent();
        if (!appId.equals(accessTokenContent.getAppId())) {
            return Result.createError("非法应用");
        }
        // 获取user
        SsoUser user = ticketGrantingTicketManager.getAndRefresh(accessTokenContent.getCodeContent().getTgt());
        if (user == null) {
            return Result.createError("服务端session已经过期");
        }
        return Result.createSuccess(generateRpcAccessToken(accessTokenContent, refreshTokenContent.getAccessToken()));
    }
}
