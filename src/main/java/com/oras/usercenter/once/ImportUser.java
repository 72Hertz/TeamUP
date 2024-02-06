package com.oras.usercenter.once;

import com.alibaba.excel.EasyExcel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 导入星球用户到数据库
 */
public class ImportUser {

    public static void main(String[] args) {
        //Excel数据文件路径
        String fileName = "/Users/endymion/code/Java/JavaProject/user-center/src/main/resources/testExcel.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<UserInfoFromExcel> userInfoList =
                EasyExcel.read(fileName).head(UserInfoFromExcel.class).sheet().doReadSync();
        System.out.println("总数 = " + userInfoList.size());
        Map<String, List<UserInfoFromExcel>> listMap =
                userInfoList.stream()
                        .filter(userInfo -> StringUtils.isNotEmpty(userInfo.getUsername()))
                        .collect(Collectors.groupingBy(UserInfoFromExcel::getUsername));
        for (Map.Entry<String, List<UserInfoFromExcel>> stringListEntry : listMap.entrySet()) {
            if (stringListEntry.getValue().size() > 1) {
                System.out.println("username = " + stringListEntry.getKey());
                System.out.println("1");
            }
        }
        System.out.println("不重复昵称数 = " + listMap.keySet().size());
    }
}
