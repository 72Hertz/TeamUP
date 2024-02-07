package com.oras.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oras.usercenter.common.ErrorCode;
import com.oras.usercenter.exception.BusinessException;
import com.oras.usercenter.model.domain.Team;
import com.oras.usercenter.model.domain.User;
import com.oras.usercenter.model.domain.UserTeam;
import com.oras.usercenter.model.enums.TeamStatusEnum;
import com.oras.usercenter.service.TeamService;
import com.oras.usercenter.mapper.TeamMapper;
import com.oras.usercenter.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

/**
* @author endymion
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2024-02-07 15:38:46
*/
@Service
@Transactional(rollbackFor = Exception.class)
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    private UserTeamService userTeamService;

    @Override
    public long addTeam(Team team, User loginUser) {
        //1.请求参数是否为空
        if(team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2.是否登录，未登录不可创建
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        final long userId = loginUser.getId();
        //3.校验队伍信息
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if(maxNum<1 || maxNum>20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数过多或不足");
        }
        //4.队伍标题校验
        String name = team.getName();
        if(StringUtils.isBlank(name) || name.length() > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍标题不符合要求（长度小于20）");
        }
        //4.队伍描述校验
        String description = team.getDescription();
        if(StringUtils.isBlank(description) && description.length() > 512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍描述过长（长度小于512）");
        }
        //5.队伍是否公开
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if(statusEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不符合要求");
        }
        //6.若为加密状态则需要设置密码
        String password = team.getPassword();
        if(TeamStatusEnum.SECRET.equals(statusEnum) &&  (StringUtils.isBlank(password) || password.length()>32)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码设置正确");
        }
        //6.设置超时时间校验：超时时间>当前时间
        Date expireTime = team.getExpireTime();
        if(new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"超时时间>当前时间");
        }
        //7.校验：用户最多可以创建5个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long hasTeamNUm = this.count(queryWrapper);
        if(hasTeamNUm >= 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"最多创建5个队伍");
        }
        //8.插入新队伍到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId = team.getId();

        if(!result || teamId==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }

        //9.插入队伍到用户队伍关系表

        UserTeam userTeam = new UserTeam();

        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());

        result = userTeamService.save(userTeam);
        if(!result){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }



        return teamId;
    }
}




