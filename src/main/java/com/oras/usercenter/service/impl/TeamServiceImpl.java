package com.oras.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oras.usercenter.model.domain.Team;
import com.oras.usercenter.service.TeamService;
import com.oras.usercenter.mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author endymion
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2024-02-07 15:38:46
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

}




