package com.oras.usercenter.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 表格用户信息
 */
@Data
@EqualsAndHashCode
public class UserInfoFromExcel {

    @ExcelProperty(index = 0)
    private String code;

    @ExcelProperty(index = 1)
    private String username;

}