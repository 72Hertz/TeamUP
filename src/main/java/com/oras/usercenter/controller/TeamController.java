package com.oras.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oras.usercenter.common.BaseResponse;
import com.oras.usercenter.common.ErrorCode;
import com.oras.usercenter.common.ResultUtils;
import com.oras.usercenter.exception.BusinessException;
import com.oras.usercenter.model.domain.Team;
import com.oras.usercenter.model.domain.User;
import com.oras.usercenter.model.dto.TeamQuery;
import com.oras.usercenter.model.request.TeamAddRequest;
import com.oras.usercenter.model.request.UserLoginRequest;
import com.oras.usercenter.model.request.UserRegisterRequest;
import com.oras.usercenter.model.vo.TeamUserVO;
import com.oras.usercenter.service.TeamService;
import com.oras.usercenter.service.UserService;
import jodd.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.oras.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 队伍接口
 * @author oras
 */
@RestController
@RequestMapping("/team")
@CrossOrigin
@Slf4j
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request ){
        if(teamAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        long teamId = teamService.addTeam(team,loginUser);
        return ResultUtils.success(teamId);

    }
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam( @RequestBody long id ){
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.removeById(id);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        return ResultUtils.success(true);

    }
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam( @RequestBody Team team ){
        if(team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.updateById(team);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败");
        }
        return ResultUtils.success(true);

    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id){

        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if(team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);

    }

//    @GetMapping("/list")
//    public BaseResponse<List<Team>> listTeam(TeamQuery teamQuery){
//        if(teamQuery == null){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//
//        }
//        Team team = new Team();
//        BeanUtils.copyProperties(team, teamQuery);
//        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
//        List<Team> teamList = teamService.list(queryWrapper);
//        return ResultUtils.success(teamList);
//    }
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeam(TeamQuery teamQuery, HttpServletRequest request){
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        boolean isAdmin = userService.isAdmin(request);
        List<TeamUserVO> teamList = teamService.listTeam(teamQuery,isAdmin);
        return ResultUtils.success(teamList);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listPageTeams(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);
        Page<Team> page = new Page<>(teamQuery.getPageNum(),teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> resultPage = teamService.page(page,queryWrapper);
        return ResultUtils.success(resultPage);
    }



}
