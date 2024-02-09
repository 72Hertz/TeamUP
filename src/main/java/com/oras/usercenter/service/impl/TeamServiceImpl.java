package com.oras.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oras.usercenter.common.ErrorCode;
import com.oras.usercenter.exception.BusinessException;
import com.oras.usercenter.model.domain.Team;
import com.oras.usercenter.model.domain.User;
import com.oras.usercenter.model.domain.UserTeam;
import com.oras.usercenter.model.dto.TeamQuery;
import com.oras.usercenter.model.enums.TeamStatusEnum;
import com.oras.usercenter.model.request.TeamJoinRequest;
import com.oras.usercenter.model.request.TeamUpdateRequest;
import com.oras.usercenter.model.vo.TeamUserVO;
import com.oras.usercenter.model.vo.UserVO;
import com.oras.usercenter.service.TeamService;
import com.oras.usercenter.mapper.TeamMapper;
import com.oras.usercenter.service.UserService;
import com.oras.usercenter.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    @Resource
    private UserService userService;

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

    @Override
    public List<TeamUserVO> listTeam(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if(teamQuery != null){
            //根据队伍id查询
            Long id = teamQuery.getId();
            if(id != null && id> 0){
                queryWrapper.eq("id",id);
            }
            //根据队伍中成员查询
            List<Long> idList = teamQuery.getIdList();
            if (CollectionUtils.isNotEmpty(idList)) {
                queryWrapper.in("id", idList);
            }
            //同时在队伍名称和队伍描述中查询
            String searchText = teamQuery.getSearchText();
            if(StringUtils.isNotBlank(searchText)){
                queryWrapper.and(qw -> qw.like("name",searchText).like("description",searchText));
            }
            //根据队伍名称查询
            String name = teamQuery.getName();
            if(StringUtils.isNotBlank(name)){
                queryWrapper.like("name",name);
            }
            //根据队伍描述查询
            String description = teamQuery.getDescription();
            if(StringUtils.isNotBlank(description)){
                queryWrapper.like("description",description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            //根据最大人数查询
            if(maxNum != null && maxNum>0){
                queryWrapper.eq("maxNum",maxNum);
            }
            //根据队长id查询
            Long userId = teamQuery.getUserId();
            if(userId !=null && userId >0){
                queryWrapper.eq("userId",userId);
            }
            //根据队伍状态查询
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if(statusEnum == null){
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if(!isAdmin && !statusEnum.equals(TeamStatusEnum.PUBLIC)){
                throw new BusinessException(ErrorCode.NO_AUTH);
            }

            queryWrapper.eq("status",statusEnum.getValue());

        }

        //不展示已过期队伍
        queryWrapper.and( qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));


        List<Team> teamList = this.list(queryWrapper);

        //关联查询创建人用户信息
        //TODO 关联查询队伍成员信息
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        if(CollectionUtils.isEmpty(teamList)){
            return new ArrayList<>();
        }
        for(Team team : teamList){
            Long userId = team.getUserId();
            if(userId == null){
                continue;
            }
            User user = userService.getById(userId);
            //脱敏用户信息
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team,teamUserVO);

            if(user != null){
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user,userVO);
                teamUserVO.setCreateUser(userVO);
            }

            teamUserVOList.add(teamUserVO);

        }

        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if(teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if( id==null || id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getById(id);
        if(oldTeam == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }

        if(oldTeam.getUserId() != loginUser.getId() && !userService.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if(statusEnum.equals(TeamStatusEnum.SECRET)){
            if(StringUtils.isBlank(teamUpdateRequest.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密的房间必须设置密码");
            }
        }


        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        return this.updateById(updateTeam);

    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if(teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamJoinRequest.getTeamId();
        if(teamId == null || teamId <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if(team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }


        Date expireTime = team.getExpireTime();
        if( expireTime != null && team.getExpireTime().before(new Date())){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if(TeamStatusEnum.PRIVATE.equals(teamStatusEnum)){
            throw new BusinessException(ErrorCode.NULL_ERROR,"私有队伍禁止加入");
        }
        String password = teamJoinRequest.getPassword();
        if(TeamStatusEnum.SECRET.equals(teamStatusEnum)){
            if(StringUtils.isBlank(password) || !team.getPassword().equals(password)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码错误");
            }
        }


        //用户已加入队伍的数量
        long userId = loginUser.getId();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId",userId);
        long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
        if(hasJoinNum>5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"最多加入5个队伍");
        }
        //不能重复加入已加入的队伍
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId",userId);
        userTeamQueryWrapper.eq("teamId", teamId);
        long hasUserJoinTeamNum = userTeamService.count(userTeamQueryWrapper);
        if(hasUserJoinTeamNum > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户已加入该队伍，不允许重复加入");
        }
        //该队伍已有人数
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        long teamHasJoinNum = userTeamService.count(userTeamQueryWrapper);
        if(teamHasJoinNum >= team.getMaxNum()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已满");
        }

        //加入队伍，修改用户信息
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }
}




