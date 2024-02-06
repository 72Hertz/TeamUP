package com.oras.usercenter.once;
import java.util.Date;

import com.oras.usercenter.mapper.UserMapper;
import com.oras.usercenter.model.domain.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

@Component
public class InsertUsers {

    @Resource
    private UserMapper userMapper;

    /**
     * 批量插入用户
     */
//    @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE)
    public void doInsertUser(){
//        final int INSERT_NUM = 1000;
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        for (int i = 0; i < INSERT_NUM; i++) {
//            User user = new User();
//
//            user.setUsername("假学生");
//            user.setUserAccount("fakeStudent");
//            user.setAvatarUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRdID5oiBcQb6vyPCTi53zY49h7mUva2NV4Tk8fVVdC-0UYH-TtkuhX&usqp=CAE&s");
//            user.setGender(0);
//            user.setUserPassword("12345678");
//            user.setEmail("123@qq.com");
//            user.setUserStatus(0);
//            user.setPhone("135111111111");
//
//            user.setUserRole(0);
//            user.setCode("2020");
//            user.setTags("[]");
//            userMapper.insert(user);
//        }
//        stopWatch.stop();
//        System.out.println(stopWatch.getTotalTimeMillis());

    }

    public static void main(String[] args) {
        new InsertUsers().doInsertUser();
    }

}
