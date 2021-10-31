package com.home.small.sso.server.session;

import com.home.small.sso.server.common.CodeContent;
import com.home.small.sso.server.common.Expiration;

import java.util.UUID;

/**
 * 授权码的管理
 */
public interface CodeManager extends Expiration {

    /**
     * 生成授权code
     * @param tgt
     * @param sendLogoutRequest
     * @param redirectUri
     * @return
     */
    default String generate(String tgt, boolean sendLogoutRequest, String redirectUri) {
        String code = "code-" + UUID.randomUUID().toString().replace("-", "");
        create(code, new CodeContent(tgt, sendLogoutRequest, redirectUri));
        return code;
    }

    /**
     * 生成授权码
     * @param code
     * @param codeContent
     */
    void create(String code, CodeContent codeContent);

    /**
     * 查找并删除
     * @param code
     * @return
     */
    CodeContent getAndRemove(String code);

    @Override
    default int getExpiresIn() {
        // code失效时间默认10分钟
        return 600;
    }
}
