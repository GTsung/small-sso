package com.home.small.sso.client.filter;

import com.alibaba.fastjson.JSON;
import com.home.small.sso.client.constants.Oauth2Constant;
import com.home.small.sso.client.constants.SsoConstant;
import com.home.small.sso.client.rpc.Result;
import com.home.small.sso.client.rpc.RpcAccessToken;
import com.home.small.sso.client.session.SessionAccessToken;
import com.home.small.sso.client.utils.Oauth2Utils;
import com.home.small.sso.client.utils.SessionUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

/**
 * @author GTsung
 * @date 2021/10/31
 */
@Slf4j
public class LoginFilter extends ClientFilter {

    @Override
    public boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionAccessToken sessionAccessToken = SessionUtils.getAccessToken(request);
        // 本地session中存在token且(token没有过期或者刷新token成功)，不再拦截
        if (sessionAccessToken != null && (!sessionAccessToken.isExpired()
                || refreshToken(sessionAccessToken.getRefreshToken(), request))) {
            return true;
        }
        // 请求参数中是否存在oauth2授权认证码
        String code = request.getParameter(Oauth2Constant.AUTH_CODE);
        if (code != null) {
            // oauth2授权码方式获取token
            getAccessToken(code, request);
            // 为去掉url中的授权参数，再跳转一次当前地址
            redirectLocalRemoveCode(request, response);
        } else {
            // 跳转认证中心登录页
            redirectLogin(request, response);
        }
        return false;
    }

    /**
     * oauth2授权码认证获取token
     *
     * @param code
     * @param request
     */
    private void getAccessToken(String code, HttpServletRequest request) {
        Result<RpcAccessToken> result = Oauth2Utils.getAccessToken(getServerUrl(), getAppId(), getAppSecret(), code);
        if (!result.isSuccess()) {
            log.error("getAccessToken has error, message:{}", result.getMessage());
            return;
        }
        // 放入到session中并绑定映射关系
        setAccessTokenInSession(result.getData(), request);
    }

    /**
     * 通过refreshToken远程调用认证中心获取新的accessToken，延长session
     *
     * @param refreshToken
     * @param request
     * @return
     */
    protected boolean refreshToken(String refreshToken, HttpServletRequest request) {
        // 去认证中心刷新token
        Result<RpcAccessToken> result = Oauth2Utils.refreshToken(getServerUrl(), getAppId(), refreshToken);
        if (!result.isSuccess()) {
            log.info("refresh token error, message: {}", result.getMessage());
            return false;
        }
        // 将新的token与session关系保存，并将新的token保存到本地session
        return setAccessTokenInSession(result.getData(), request);
    }

    /**
     * 记录token到session中，并保存session与token的对应关系
     *
     * @param rpcAccessToken
     * @param request
     * @return
     */
    private boolean setAccessTokenInSession(RpcAccessToken rpcAccessToken, HttpServletRequest request) {
        if (rpcAccessToken == null) {
            return false;
        }
        // 记录token到本地session
        SessionUtils.setAccessToken(request, rpcAccessToken);

        // 记录session与token映射关系
        recordSessionTokenMap(request, rpcAccessToken.getAccessToken());
        return true;
    }

    /**
     * 记录session与token的映射关系
     *
     * @param request
     * @param accessToken
     */
    private void recordSessionTokenMap(HttpServletRequest request, String accessToken) {
        final HttpSession session = request.getSession();
        // 先移除再添加
        getSessionMappingStorage().removeBySessionId(session.getId());
        getSessionMappingStorage().addSessionById(accessToken, session);
    }

    /**
     * oauth2授权码方式获取token后重新跳转到本地址去除code参数
     *
     * @param request
     * @param response
     */
    private void redirectLocalRemoveCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String currentUrl = getCurrentUrl(request);
        currentUrl = currentUrl.substring(0, currentUrl.indexOf(Oauth2Constant.AUTH_CODE) - 1);
        response.sendRedirect(currentUrl);
    }

    /**
     * 获取当前地址
     *
     * @param request
     * @return
     */
    private String getCurrentUrl(HttpServletRequest request) {
        return new StringBuilder().append(request.getRequestURI())
                .append(request.getQueryString() == null ? "" : "?" + request.getQueryString()).toString();
    }

    /**
     * 跳转到认证中心的登录页
     * @param request
     * @param response
     */
    private void redirectLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (isAjaxRequest(request)) {
            responseJson(response, SsoConstant.NO_LOGIN, "未登录或者已超时");
        } else {
            // 跳转至认证中心login页
            String loginUrl = new StringBuilder().append(getServerUrl()).append(SsoConstant.LOGIN_URL)
                    // 当前appId
                    .append("?").append(Oauth2Constant.APP_ID).append("=").append(getAppId())
                    // 跳转地址传给认证中心用于登陆成功后跳转回来
                    .append("&").append(SsoConstant.REDIRECT_URI).append("=")
                    .append(URLEncoder.encode(getCurrentUrl(request), "utf-8")).toString();
            response.sendRedirect(loginUrl);
        }
    }

    protected void responseJson(HttpServletResponse response, int code, String msg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(200);
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(Result.create(code, msg)));
        writer.flush();
        writer.close();
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWith);
    }
}