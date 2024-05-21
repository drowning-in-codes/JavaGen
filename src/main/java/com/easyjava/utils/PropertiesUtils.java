package com.easyjava.utils;/**
 * @Author: proanimer
 * @Description:
 * @Date: Created in 2024/5/8
 * @Modified By
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @projectName: workspace
 * @package: com.easyjava.utils
 * @className: PropertiesUtils
 * @author: proanimer
 * @description:
 * @date: 2024/5/8 22:04
 */
public class PropertiesUtils {
    private static Properties props = new Properties();
    private static Map<String, String> PROPER_MAP = new ConcurrentHashMap<>();
    static {
        InputStream is = null;
        try {
            is = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties");
            assert is != null;
            props.load(new InputStreamReader(is, StandardCharsets.UTF_8));
            for (Object o : props.keySet()) {
                String key = (String) o;
                PROPER_MAP.put(key, props.getProperty(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static String getString(String key) {
        return PROPER_MAP.get(key);
    }
    public static void main(String[] args) {
        System.out.println(getString("db.driver.name"));

    }
}
