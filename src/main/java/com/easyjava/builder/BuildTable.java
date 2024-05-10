package com.easyjava.builder;/**
 * @Author: proanimer
 * @Description:
 * @Date: Created in 2024/5/8
 * @Modified By
 */

import com.easyjava.beans.Constants;
import com.easyjava.beans.FieldInfo;
import com.easyjava.beans.TableInfo;
import com.easyjava.utils.JsonUtils;
import com.easyjava.utils.PropertiesUtils;
import com.easyjava.utils.StringUtils;
import com.mysql.cj.xdevapi.Table;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @projectName: workspace
 * @package: com.easyjava.builder
 * @className: BuildTable
 * @author: proanimer
 * @description:
 * @date: 2024/5/8 22:16
 */
public class BuildTable {
    private static Connection conn = null;
    private static final Logger logger = LoggerFactory.getLogger(BuildTable.class);
    private static final String SQL_SHOW_TABLE_STATUS = "SHOW TABLE STATUS";
    private static final String SQL_SHOW_TABLE_FIELDS = "SHOW FULL FIELDS FROM %s";

    static {
        String diverName = PropertiesUtils.getString("db.driver.name");
        String url = PropertiesUtils.getString("db.url");
        String username = PropertiesUtils.getString("db.username");
        String password = PropertiesUtils.getString("db.password");
        try {
            Class.forName(diverName);
            conn = DriverManager.getConnection(url, username, password);

        } catch (Exception e) {
            logger.error("数据库连接失败", e);
        }
    }

    public static void getTables() {
        PreparedStatement ps = null;
        ResultSet tableResult = null;
        List<TableInfo> tableInfoList = new ArrayList<>();

        try {
            ps = conn.prepareStatement(SQL_SHOW_TABLE_STATUS);
            tableResult = ps.executeQuery();
            while (tableResult.next()) {
                String tableName = tableResult.getString("name");
                String comment = tableResult.getString("comment");
                logger.info("表名：{}，注释：{}", tableName, comment);
                TableInfo tableInfo = new TableInfo();
                tableInfo.setTableName(tableName);
                String beanName = tableName;
                if (Constants.IGNORE_TABLE_PREFIX) {
                    beanName = tableName.substring(tableName.indexOf("_") + 1);
                    logger.info("bean_name:{}", beanName);
                }
                beanName = processField(beanName, true);
                tableInfo.setBeanName(beanName);
                tableInfo.setComment(comment);
                tableInfo.setBeanParamName(beanName + Constants.SUFFIX_BEAN_PARAM);
                List<FieldInfo> fieldInfoList = readFieldInfo(tableInfo);
                logger.info(JsonUtils.convertObj2Json(tableInfo));
                logger.info(JsonUtils.convertObj2Json(fieldInfoList));

                tableInfoList.add(tableInfo);

            }
        } catch (Exception e) {
            logger.error("读取表失败", e);

        } finally {
            if (tableResult != null) {
                try {
                    tableResult.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {

    }

    public static List<FieldInfo> readFieldInfo(TableInfo tableInfo) {
        PreparedStatement ps = null;
        ResultSet fieldResult = null;
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        try {
            ps = conn.prepareStatement(String.format(SQL_SHOW_TABLE_FIELDS,tableInfo.getTableName()));
            fieldResult = ps.executeQuery();
            while (fieldResult.next()) {
                String field = fieldResult.getString("Field");
                String type = fieldResult.getString("type");
                String extra = fieldResult.getString("extra");
                String comment = fieldResult.getString("comment");
                logger.info("id:{} type:{} extra{} comment:{}", field, type, extra, comment);
                if (type.indexOf("(") > 0) {
                    type = type.substring(0, type.indexOf("("));
                }
                String propertyName = processField(field, false);
                FieldInfo fieldInfo = new FieldInfo();
                fieldInfo.setFieldName(field);
                fieldInfo.setComment(comment);
                fieldInfo.setSqlType(type);
                fieldInfo.setAutoIncrement("auto_increment".equalsIgnoreCase(extra));
                fieldInfo.setPropertyName(propertyName);
                String javaType = fieldInfo.getSqlType();
                fieldInfo.setJavaType(processJavaType(javaType));
                if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, type)) {
                    tableInfo.setHaveDateTime(true);
                }
                if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, type)) {
                    tableInfo.setHaveDate(true);
                }
                if (ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES, type)) {
                    tableInfo.setHaveBigDecimal(true);
                }
                fieldInfoList.add(fieldInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (fieldResult != null) {
                try {
                    fieldResult.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return fieldInfoList;
    }

    private static String processField(String field, Boolean upperCaseFirstLetter) {
        StringBuilder sb = new StringBuilder();
        String[] fields = field.split("_");
        for (int index = 0; index < fields.length; index++) {
            if ((index == 0) && (upperCaseFirstLetter)) {
                sb.append(StringUtils.upperCaseFirstLetter(fields[index]));
            } else {
                sb.append(StringUtils.upperCaseFirstLetter(fields[index]));
            }
        }
        return sb.toString();
    }

    private static String processJavaType(String type) {
        if (ArrayUtils.contains(Constants.SQL_INTEGER_TYPES, type)) {
            return "Integer";
        } else if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, type)) {
            return "STRING";
        } else if (ArrayUtils.contains(Constants.SQL_LONG_TYPES, type)) {
            return "Date";
        } else if (ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES, type)) {
            return "BigDecimal";
        }
        else {
            throw new RuntimeException("无法识别的类型" + type);
        }
    }
}
