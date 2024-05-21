package com.easyjava.beans;/**
 * @Author: proanimer
 * @Description:
 * @Date: Created in 2024/5/10
 * @Modified By
 */

import com.easyjava.utils.PropertiesUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    private static final String PATH_JAVA = "java";
    private static final String PATH_RESOURCES = "resources";
    public static String PATH_BASE;
    public static String PACKAGE_BASE;
    public static String PATH_PO;
    public static String PACKAGE_PO;
    public static String AUTHOR_COMMENT;
    //
    public static String IGNORE_BEAN_TOJSON_ID;
    //
    public static String IGNORE_BEAN_TOJSON_EXPRESSION;
    public static String IGNORE_BEAN_TOJSON_CLASS;

    public static String BEAN_DATE_FORMAT_EXPRESSION;
    public static String BEAN_DATE_FORMAT_CLASS;

    public static String BEAN_DATE_PARSE_EXPRESSION;
    public static String BEAN_DATE_PARSE_CLASS;

    public static String PACKAGE_UTILS;
    public static String PATH_UTILS;

    public static String PATH_ENUM;
    public static String PACKAGE_ENUM;

    public static String PATH_QUERY;
    public static String PACKAGE_QUERY;
    public static String SUFFIX_BEAN_QUERY;

    public static String SUFFIX_BEAN_QUERY_FUZZY;
    public static String SUFFIX_BEAN_QUERY_START;
    public static String SUFFIX_BEAN_QUERY_END;

    public static String PACKAGE_MAPPER;
    public static String PATH_MAPPER;
    public static String SUFFIX_MAPPERS;

    public static String PATH_MAPPERS_XML;

    static {

        IGNORE_TABLE_PREFIX = Boolean.valueOf(PropertiesUtils.getString("ignore.table.prefix"));
        SUFFIX_BEAN_PARAM = PropertiesUtils.getString("suffix.bean.param");

        PACKAGE_BASE = PropertiesUtils.getString("package.base");
        PACKAGE_PO = Paths.get(PACKAGE_BASE) + "." + PropertiesUtils.getString("package.po");

        PATH_BASE = Paths.get(PropertiesUtils.getString("path.base")).resolve(PATH_JAVA).toString().replace(".", File.separator);
        PATH_PO = Paths.get(PATH_BASE).resolve(PACKAGE_PO).toString().replace(".", File.separator);

        IGNORE_BEAN_TOJSON_ID = PropertiesUtils.getString("ignore.bean.tojson.id");
        IGNORE_BEAN_TOJSON_EXPRESSION = PropertiesUtils.getString("ignore.bean.tojson.expression");
        IGNORE_BEAN_TOJSON_CLASS = PropertiesUtils.getString("ignore.bean.tojson.class");

        BEAN_DATE_FORMAT_EXPRESSION = PropertiesUtils.getString("bean.date.format.expression");
        BEAN_DATE_FORMAT_CLASS = PropertiesUtils.getString("bean.date.format.class");

        BEAN_DATE_PARSE_EXPRESSION = PropertiesUtils.getString("bean.date.parse.expression");
        BEAN_DATE_PARSE_CLASS = PropertiesUtils.getString("bean.date.parse.class");


        PACKAGE_UTILS = Paths.get(PACKAGE_BASE) + "." + PropertiesUtils.getString("package.utils");
        PATH_UTILS = Paths.get(PATH_BASE).resolve(PACKAGE_UTILS).toString().replace(".", File.separator);

        PACKAGE_ENUM = Paths.get(PACKAGE_BASE) + "." + PropertiesUtils.getString("package.enums");
        PATH_ENUM = Paths.get(PATH_BASE).resolve(PACKAGE_ENUM).toString().replace(".", File.separator);

        PACKAGE_QUERY = Paths.get(PACKAGE_BASE) + "." + PropertiesUtils.getString("package.query");
        PATH_QUERY = Paths.get(PATH_BASE).resolve(PACKAGE_QUERY).toString().replace(".", File.separator);

        PACKAGE_MAPPER = Paths.get(PACKAGE_BASE) + "." + PropertiesUtils.getString("package.mapper");
        PATH_MAPPER = Paths.get(PATH_BASE).resolve(PACKAGE_MAPPER).toString().replace(".", File.separator);
        SUFFIX_BEAN_QUERY = PropertiesUtils.getString("suffix.bean.query");
        SUFFIX_BEAN_QUERY_FUZZY = PropertiesUtils.getString("suffix.bean.query.fuzzy");
        SUFFIX_BEAN_QUERY_START = PropertiesUtils.getString("suffix.bean.query.time.start");
        SUFFIX_BEAN_QUERY_END = PropertiesUtils.getString("suffix.bean.query.time.end");

        PATH_MAPPERS_XML = Paths.get(PropertiesUtils.getString("path.base")).resolve(PATH_RESOURCES).resolve(PACKAGE_MAPPER).toString().replace(".", File.separator);
        SUFFIX_MAPPERS = PropertiesUtils.getString("suffix.mappers");
        AUTHOR_COMMENT = PropertiesUtils.getString("author.comment");
    }

    public static final String[] SQL_DATE_TIME_TYPES = new String[]{"datetime", "timestamp"};
    public static final String[] SQL_DATE_TYPES = new String[]{"date"};
    public static final String[] SQL_DECIMAL_TYPES = new String[]{"decimal", "double", "float"};
    public static final String[] SQL_STRING_TYPES = new String[]{"char", "varchar", "text", "mediumtext", "longtext"};
    public static final String[] SQL_INTEGER_TYPES = new String[]{"int", "tinyint"};
    public static final String[] SQL_LONG_TYPES = new String[]{"bigint"};

    public static void main(String[] args) {
        System.out.println(PATH_MAPPERS_XML);
    }
}
