package com.easyjava.utils;
/**
 * @Author: proanimer
 * @Description:
 * @Date: Created in 2024/5/11
 * @Modified By proanimer
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @projectName: workspace
 * @package: com.easyjava.utils
 * @className: DateUtils
 * @author: proanimer
 * @description:
 * @date: 2024/5/11 19:35
 */
public class DateUtils {
    private static final Logger log = LoggerFactory.getLogger(DateUtils.class);
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String _YYYYMMDD = "yyyy/MM/dd";
    public static String format(Date date, String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static Date parse(String date, String pattern) {
        try {
            new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException e) {
            log.error(e.toString());
        }
        return null;
    }
}
