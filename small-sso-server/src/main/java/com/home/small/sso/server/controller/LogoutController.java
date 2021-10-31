package com.home.small.sso.server.controller;

import com.home.small.sso.client.constants.SsoConstant;
import com.home.small.sso.server.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author GTsung
 * @date 2021/10/31
 */
@Controller
@RequestMapping("/logout")
public class LogoutController {

    @Autowired
    private SessionManager sessionManager;

    @RequestMapping(method = RequestMethod.GET)
    public String logout(@RequestParam(value = SsoConstant.REDIRECT_URI)String redirectUri,
                         HttpServletRequest request, HttpServletResponse response) {
        sessionManager.invalidate(request, response);
        return "redirect:" + redirectUri;
    }
}
