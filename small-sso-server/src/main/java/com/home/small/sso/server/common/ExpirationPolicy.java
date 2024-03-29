package com.home.small.sso.server.common;

/**
 * 有效期限管理
 */
public interface ExpirationPolicy {

    /**
     * 每5分钟执行一次
     */
    String SCHEDULED_CRON = "0 */5 * * * ?";

    /**
     * 定时清理
     */
    void verifyExpired();
}
