package com.home.small.sso.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 用户
 *
 * @author GTsung
 * @date 2021/10/31
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 10125567610925057L;

    private Integer id;

    /**
     * 姓名(昵称)
     */
    private String name;

    /**
     * 用户名(登录名)
     */
    private String username;
    /**
     * 密码
     */
    private String password;

}
