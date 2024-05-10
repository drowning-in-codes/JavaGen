package com.easyjava.utils;/**
 * @Author: proanimer
 * @Description:
 * @Date: Created in 2024/5/10
 * @Modified By
 */

/**
 * @projectName: workspace
 * @package: com.easyjava.utils
 * @className: StringUtils
 * @author: proanimer
 * @description:
 * @date: 2024/5/10 14:45
 */
public class StringUtils {
    public static String upperCaseFirstLetter(String field) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(field)) {
            return field;
        }
        return field.substring(0, 1).toUpperCase() + field.substring(1);
    }
    public static String lowerCaseFirstLetter(String field) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(field)) {
            return field;
        }
        return field.substring(0, 1).toLowerCase() + field.substring(1);
    }
    public static void main(String[] args) {
        System.out.println(upperCaseFirstLetter("company"));
    }
}
