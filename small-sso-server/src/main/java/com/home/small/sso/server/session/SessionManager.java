package com.home.small.sso.server.session;

import com.home.small.sso.server.constant.AppConstant;
import com.home.small.sso.server.util.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author GTsung
 * @date 2021/10/31
 */
@Component
public class SessionManager {

    @Autowired
    private TicketGrantingTicketManager ticketGrantingTicketManager;

    /**
     * 获取ticketGrantTicket
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
}
