package com.oras.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 * @author oras
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -6200503009689812137L;

    private String userAccount;
    private String userPassword;




}
