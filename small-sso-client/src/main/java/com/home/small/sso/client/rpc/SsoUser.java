package com.home.small.sso.client.rpc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 已登录用户信息
 * @author GTsung
 * @date 2021/10/30
 */
@Setter
@Getter
@AllArgsConstructor
public class SsoUser implements Serializable {

    private static final long serialVersionUID = 1764365572138947234L;

    /**
     * 登录成功的userId
     */
    private Integer id;

    /**
     * 登录名
     */
    private String username;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SsoUser other = (SsoUser) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }
}
