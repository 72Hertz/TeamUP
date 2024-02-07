package com.oras.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oras.usercenter.model.domain.UserTeam;
import com.oras.usercenter.service.UserTeamService;
import com.oras.usercenter.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author endymion
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-02-07 15:40:57
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




