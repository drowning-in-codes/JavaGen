package com.easyjava.utils;/**
 * @Author: proanimer
 * @Description:
 * @Date: Created in 2024/5/10
 * @Modified By
 */


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @projectName: workspace
 * @package: com.easyjava.utils
 * @className: JsonUtils
 * @author: proanimer
 * @description:
 * @date: 2024/5/10 16:26
 */
public class JsonUtils {
    public static String convertObj2Json(Object obj) {
        if (null == obj) {
            return null;
        }
        return JSON.toJSONString(obj, SerializerFeature.DisableCircularReferenceDetect);
    }
}
