package com.oras.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出队伍请求体
 */
@Data
public class TeamQuitRequest implements Serializable {
    private static final long serialVersionUID = -4383990435167045464L;
    /**
     * 用户要退出的队伍id
     */
    private Long teamId;

}
