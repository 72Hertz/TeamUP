package com.oras.usercenter.service;

import com.oras.usercenter.mapper.UserMapper;
import com.oras.usercenter.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class InsertUsersTest {

    @Resource
    private UserService userService;
    private ExecutorService executorService = new ThreadPoolExecutor(16, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));
    /**
     * 批量插入用户
     */
    @Test
    public void doInsertUser() {
        final int INSERT_NUM = 100000;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();

            user.setUsername("假学生");
            user.setUserAccount("fakeStudent");
            user.setAvatarUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRdID5oiBcQb6vyPCTi53zY49h7mUva2NV4Tk8fVVdC-0UYH-TtkuhX&usqp=CAE&s");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setEmail("123@qq.com");
            user.setUserStatus(0);
            user.setPhone("135111111111");

            user.setUserRole(0);
            user.setCode("2020");
            user.setTags("[]");
            userList.add(user);
        }
        userService.saveBatch(userList,10000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
    /**
     * 并发批量插入用户
     */
    @Test
    public void doConcurrencyInsertUser() {
        final int INSERT_NUM = 100000;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        //分10组
        int j=0;
        int batchSize = 5000;



        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM/batchSize; i++) {
            List<User> userList = new ArrayList<>();
            while (true){
                j++;

                User user = new User();
                user.setUsername("假学生");
                user.setUserAccount("fakeStudent");
                user.setAvatarUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRdID5oiBcQb6vyPCTi53zY49h7mUva2NV4Tk8fVVdC-0UYH-TtkuhX&usqp=CAE&s");
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setEmail("123@qq.com");
                user.setUserStatus(0);
                user.setPhone("135111111111");

                user.setUserRole(0);
                user.setCode("2020");
                user.setTags("[]");
                userList.add(user);

                if(j % batchSize ==0){
                    break;
                }
            }
            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                userService.saveBatch(userList, batchSize);
            },executorService);
            futureList.add(completableFuture);

        }

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();

        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
