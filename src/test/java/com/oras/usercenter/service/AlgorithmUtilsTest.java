package com.oras.usercenter.service;

import com.oras.usercenter.utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * 随机匹配功能测试
 */
@SpringBootTest
public class AlgorithmUtilsTest {

    @Test
    void testTags(){

        List<String> tag1 = Arrays.asList("Java","大一","男");
        List<String> tag2 = Arrays.asList("Java","大二","女");
        List<String> tag3 = Arrays.asList("Python","大二","女");


        int distance1 = AlgorithmUtils.minDistance(tag1, tag2);
        int distance2 = AlgorithmUtils.minDistance(tag1, tag3);
        System.out.println("1-2: "+distance1);
        System.out.println("1-3: "+distance2);
    }





}
