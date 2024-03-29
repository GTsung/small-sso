package com.home.small.sso.server.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author GTsung
 * @date 2021/10/31
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieUtils {

    /**
     * 通过名称获取cookie
     *
     * @param request
     * @param name
     * @return
     */
    public static String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 添加cookie
     *
     * @param name
     * @param value
     * @param path
     * @param request
     * @param response
     */
    public static void addCookie(String name, String value, String path,
                                 HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        if (path != null) {
            cookie.setPath(path);
        }
        if ("https".equals(request.getScheme())) {
            cookie.setSecure(true);
        }
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }


    /**
     * 移除cookie
     * @param name
     * @param path
     * @param response
     */
    public static void removeCookie(String name, String path,
                                    HttpServletResponse response) {
        Cookie cookie = new Cookie(name, null);
        if (path != null) {
            cookie.setPath(path);
        }
        cookie.setMaxAge(-1000);
        response.addCookie(cookie);
    }


}
