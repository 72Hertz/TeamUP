package com.oras.usercenter.service;

import com.oras.usercenter.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.oras.usercenter.model.domain.User;
import com.oras.usercenter.model.dto.TeamQuery;
import com.oras.usercenter.model.vo.TeamUserVO;

import java.util.List;

/**
* @author endymion
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-02-07 15:38:46
*/
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 搜索队伍
     * @param teamQuery
     * @return
     */
    List<TeamUserVO> listTeam(TeamQuery teamQuery, boolean isAdmin);
}
