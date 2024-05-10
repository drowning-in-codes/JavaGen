package com.easyjava.beans;/**
 * @Author: proanimer
 * @Description:
 * @Date: Created in 2024/5/10
 * @Modified By
 */

import com.easyjava.utils.PropertiesUtils;

/**
 * @projectName: workspace
 * @package: com.easyjava.beans
 * @className: Constants
 * @author: proanimer
 * @description:
 * @date: 2024/5/10 14:25
 */
public class Constants {
    public static Boolean IGNORE_TABLE_PREFIX;
    public static String SUFFIX_BEAN_PARAM;
    static {
        IGNORE_TABLE_PREFIX = Boolean.valueOf(PropertiesUtils.getString("ignore.table.prefix"));
        SUFFIX_BEAN_PARAM = PropertiesUtils.getString("suffix.bean.param");
    }

    public static final String[] SQL_DATE_TIME_TYPES = new String[]{"datetime", "timestamp"};
    public static final String[] SQL_DATE_TYPES = new String[]{"date"};
    public static final String[] SQL_DECIMAL_TYPES = new String[]{"decimal","double","float"};
    public static final String[] SQL_STRING_TYPES = new String[]{"char","varchar","text","mediumtext","longtext"};
    public static final String[] SQL_INTEGER_TYPES = new String[]{"int","tinyint"};
    public static final String[] SQL_LONG_TYPES = new String[]{"bigint"};

}
