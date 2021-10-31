package com.home.small.sso.server.session;

import com.home.small.sso.client.rpc.SsoUser;
import com.home.small.sso.server.constant.AppConstant;
import com.home.small.sso.server.util.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author GTsung
 * @date 2021/10/31
 */
@Component
public class SessionManager {

    @Autowired
    private TicketGrantingTicketManager ticketGrantingTicketManager;

    @Autowired
    private AccessTokenManager accessTokenManager;

    /**
     * 获取ticketGrantTicket
     *
     * @param request
     * @return
     */
    public String getTgt(HttpServletRequest request) {
        String tgt = getCookieTgt(request);
        if (StringUtils.isEmpty(tgt) || ticketGrantingTicketManager.getAndRefresh(tgt) == null) {
            return null;
        }
        return tgt;
    }

    private String getCookieTgt(HttpServletRequest request) {
        return CookieUtils.getCookie(request, AppConstant.TGT);
    }

    /**
     * 生成TGT，并将user与tgt进行缓存
     *
     * @param user
     * @param request
     * @param response
     * @return
     */
    public String setUser(SsoUser user, HttpServletRequest request, HttpServletResponse response) {
        String tgt = getCookieTgt(request);
        if (StringUtils.isEmpty(tgt)) {
            // 生成TGT，并缓存TGT与user的对应关系
            tgt = ticketGrantingTicketManager.generate(user);
            // 放到Cookie中
            CookieUtils.addCookie(AppConstant.TGT, tgt, "/", request, response);
            // 是否过期，如果没过期则延长有效期，否则重新将user和TGT的对应关系缓存
        } else if (ticketGrantingTicketManager.getAndRefresh(tgt) == null) {
            ticketGrantingTicketManager.create(tgt, user);
        } else {
            // 否则更新用户信息
            ticketGrantingTicketManager.setUser(tgt, user);
        }
        return tgt;
    }

    /**
     * 销毁session
     * @param request
     * @param response
     */
    public void invalidate(HttpServletRequest request, HttpServletResponse response) {
        String tgt = getCookieTgt(request);
        if (StringUtils.isEmpty(tgt)) {
            return;
        }
        // 删除登陆凭证TGT
        ticketGrantingTicketManager.removeTgt(tgt);
        // 删除cookie
        CookieUtils.removeCookie(AppConstant.TGT, "/", response);
        // 删除TGT对应的所有凭证
        accessTokenManager.removeByTgt(tgt);
    }
}
