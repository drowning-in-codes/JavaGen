package com.easyjava.builder;
/**
 * @Author: proanimer
 * @Description:
 * @Date: Created in 2024/5/18
 * @Modified By proanimer
 */


import com.easyjava.beans.Constants;
import com.easyjava.beans.FieldInfo;
import com.easyjava.beans.TableInfo;
import com.easyjava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @projectName: workspace
 * @package: com.easyjava.builder
 * @className: BuildMapperXml
 * @author: proanimer
 * @description:
 * @date: 2024/5/18 15:22
 */
public class BuildMapperXml {
    private static final Logger logger = LoggerFactory.getLogger(BuildMapperXml.class);
    public static String BASE_COLUMN_LIST = "base_column_list";
    public static String BASE_QUERY_CONDITION = "base_query_condition";
    public static String QUERY_CONDITION = "query_condition";
    public static String BASE_QUERY_CONDITION_EXTEND = "base_query_condition_extend";

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_MAPPERS_XML);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String className = tableInfo.getBeanName() + Constants.SUFFIX_MAPPERS;
        File poFile = new File(folder, className + ".xml");
        OutputStream outputStream = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;

        try {
            outputStream = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            bw = new BufferedWriter(outw);
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                    "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EM\"\n" +
                    "        \"https://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
            bw.newLine();
            bw.write("<mapper namespace=\"" + Constants.PACKAGE_MAPPER + "." + className + "\">");
            bw.newLine();
            bw.write("\t<!-- 实体映射 -->");
            bw.newLine();
            String poClass = Constants.PACKAGE_PO + "." + tableInfo.getBeanName();
            String queryClass = Constants.PACKAGE_QUERY + "." + tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY;
            bw.write("\t<resultMap id=\"base_result_map\" type=\"" + poClass + "\">");
            bw.newLine();
            FieldInfo idField = null;
            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                if (Objects.equals("PRIMARY", entry.getKey())) {
                    List<FieldInfo> fieldInfoList = entry.getValue();
                    if (fieldInfoList.size() == 1) {
                        idField = fieldInfoList.get(0);
                        break;
                    }
                }
            }

            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                String comment = fieldInfo.getComment();
                if (Objects.equals(comment, null)) {
                    comment = "";
                }
                bw.write("\t\t<!-- " + comment + " -->");
                bw.newLine();
                String key = "";
                if (idField != null && fieldInfo.getPropertyName().equals(idField.getPropertyName())) {
                    key = "id";
                } else {
                    key = "result";
                }
                bw.write("\t\t<" + key + " column=\"" + fieldInfo.getFieldName() + "\" property=\"" + fieldInfo.getPropertyName() + "\"/>");
                bw.newLine();
            }
            bw.newLine();
            bw.write("\t</resultMap>");
            bw.newLine();
            // 通用查询结果列
            bw.write("\t<!-- 通用查询结果列 -->");
            bw.newLine();
            bw.write("\t<sql id=\"" + BASE_COLUMN_LIST + "\">");
            bw.newLine();
            StringBuilder columnBuilder = new StringBuilder();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                columnBuilder.append(fieldInfo.getFieldName()).append(",");
            }
            int SplitIndex = columnBuilder.lastIndexOf(",") == -1 ? 0 : columnBuilder.lastIndexOf(",");
            String columnBuilderStr = columnBuilder.substring(0, SplitIndex);
            bw.write("\t\t" + columnBuilderStr);
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();
            // 基础查询条件
            bw.write("\t<!-- 基础查询条件 -->");
            bw.newLine();
            bw.write("\t<sql id=\"" + BASE_QUERY_CONDITION + "\">");
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                columnBuilder.append(fieldInfo.getFieldName()).append(",");
            }

            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                String stringQuery = "";
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                    stringQuery = " and query." + fieldInfo.getPropertyName() + "!=''";
                }
                bw.newLine();
                bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + " != null" + stringQuery + "\">");
                bw.newLine();
                bw.write("\t\t\tand " + fieldInfo.getFieldName() + " = #{query." + fieldInfo.getPropertyName() + "}");
                bw.newLine();
                bw.write("\t\t</if>");
            }
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();

            bw.write("\t<!-- 扩展的查询条件 -->");
            bw.newLine();
            bw.write("\t<sql id=\"" + BASE_QUERY_CONDITION_EXTEND + "\">");
            for (FieldInfo fieldInfo : tableInfo.getFieldExtendList()) {
                String andWhere = "";
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                    andWhere = "and " + fieldInfo.getFieldName() + " like concat('%', #{query." + fieldInfo.getPropertyName() + "}, '%')";
                } else if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, fieldInfo.getSqlType()) || ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType())) {
                    if (fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_QUERY_START)) {
                        andWhere = "<![CDATA[ and " + fieldInfo.getFieldName() + ">=str_to_date(#{query." + fieldInfo.getPropertyName() + "}, '%Y-%m-%d') ]]>";
                    } else if (fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_QUERY_END)) {
                        andWhere = "<![CDATA[ and " + fieldInfo.getFieldName() + "< data_sub(str_to_date(#{query." + fieldInfo.getPropertyName() + "},'%Y-%m-%d'),interval -1 day) ]]>";
                    }
                }
                bw.newLine();
                bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + " != null and query." + fieldInfo.getPropertyName() + " != ''\">");
                bw.newLine();
                bw.write("\t\t\t" + andWhere);
                bw.newLine();
                bw.write("\t\t</if>");
            }
            bw.newLine();
            bw.write("\t</sql>");

            // 通用查询条件
            bw.newLine();
            bw.write("\t<!-- 通用查询条件 -->");
            bw.newLine();
            bw.write("\t<sql id=\"" + QUERY_CONDITION + "\">");
            bw.newLine();
            bw.write("\t\t<where>");
            bw.newLine();
            bw.write("\t\t\t<include refid=\"" + BASE_QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t\t\t<include refid=\"" + BASE_QUERY_CONDITION_EXTEND + "\"/>");
            bw.newLine();
            bw.write("\t\t</where>");
            bw.newLine();
            bw.write("\t</sql>");

            // 查询列表
            bw.newLine();
            bw.write("\t<!-- 查询列表 -->");
            bw.newLine();
            bw.write("\t<select id=\"selectList\" resultMap=\"base_result_map\">");
            bw.newLine();
            bw.write("\t\tSELECT <include refid=\"" + BASE_COLUMN_LIST + "\"/> FROM " + tableInfo.getTableName() + " <include refid=\"" + QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t\t<if test=\"query.orderBy!=null\">order by ${query.orderBy}</if>");
            bw.newLine();
            bw.write("\t\t<if test=\"query.simplePage!=null\">limit #{query.simplePage.start},#{query.simplePage.end}</if>");
            bw.newLine();
            bw.write("\t</select>");
            bw.newLine();
            // 查询数量
            bw.write("\t<!-- 查询数量 -->");
            bw.newLine();
            bw.write("\t<select id=\"selectCount\" resultType=\"Integer\">");
            bw.newLine();
            bw.write("\t\tSELECT count(1) FROM " + tableInfo.getTableName() + " <include refid=\"" + QUERY_CONDITION + "\"/>");
            bw.newLine();
            bw.write("\t</select>");
            bw.newLine();

            //单个插入
            bw.write("\t<!-- 插入(匹配有值的字段) -->");
            bw.newLine();
            bw.write("\t<insert id=\"insert\" parameterType=\"" + poClass + "\">");
            FieldInfo autoIncrementFieldInfo = null;
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (fieldInfo.getAutoIncrement() != null && fieldInfo.getAutoIncrement()) {
                    autoIncrementFieldInfo = fieldInfo;
                }
            }

            if (autoIncrementFieldInfo != null) {
                bw.write("\t\t<selectKey keyProperty=\"bean." + autoIncrementFieldInfo.getPropertyName() + "\" resultType=\"" + autoIncrementFieldInfo.getJavaType() + "\" order=\"AFTER\">");
                bw.newLine();
                bw.write("\t\t\tSELECT LAST_INSERT_ID()");
                bw.newLine();
                bw.write("\t\t</selectKey>");

            }
            bw.newLine();
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (autoIncrementFieldInfo != null && Objects.equals(fieldInfo.getFieldName(), autoIncrementFieldInfo.getFieldName())) {
                    continue;
                }
                bw.newLine();
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + ",");
                bw.newLine();
                bw.write("\t\t\t</if>");
            }
            bw.newLine();
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">");
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.newLine();
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + "#{bean." + fieldInfo.getPropertyName() + "}" + ",");
                bw.newLine();
                bw.write("\t\t\t</if>");
            }
            bw.newLine();
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();

            // 插入更新
            bw.write("\t<!-- 插入或者更新 (匹配有值的字段) -->");
            bw.newLine();
            bw.write("\t<insert id=\"insertOrUpdate\" parameterType=\"" + poClass + "\">");
            bw.newLine();
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.newLine();
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + ",");
                bw.newLine();
                bw.write("\t\t\t</if>");
            }
            bw.newLine();
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">");
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.newLine();
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + "#{bean." + fieldInfo.getPropertyName() + "}" + ",");
                bw.newLine();
                bw.write("\t\t\t</if>");
            }
            bw.newLine();
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\ton DUPLICATE key update");
            bw.newLine();
            bw.write("\t\t<trim prefix=\"\" suffix=\"\" suffixOverrides=\",\">");
            Map<String, String> keyTempMap = new HashMap<>();
            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                List<FieldInfo> fieldInfoList = entry.getValue();
                for (FieldInfo item : fieldInfoList) {
                    keyTempMap.put(item.getFieldName(), item.getFieldName());
                }

            }
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (keyTempMap.get(fieldInfo.getFieldName()) != null) {
                    continue;
                }
                bw.newLine();
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + " = VALUES(" + fieldInfo.getFieldName() + "),");
                bw.newLine();
                bw.write("\t\t\t</if>");
            }
            bw.newLine();
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t</insert>");

            // 批量插入
            bw.newLine();
            bw.write("\t<!-- 批量插入-->");
            bw.newLine();
            bw.write("\t<insert id=\"insertBatch\" parameterType=\"" + poClass + "\">");
            bw.newLine();
            StringBuilder insertFieldBuilder = new StringBuilder();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (fieldInfo.getAutoIncrement()) {
                    continue;
                }
                insertFieldBuilder.append(fieldInfo.getFieldName()).append(",");
            }
            SplitIndex = insertFieldBuilder.lastIndexOf(",") == -1 ? 0 : insertFieldBuilder.lastIndexOf(",");
            String insertFieldStr = insertFieldBuilder.substring(0, SplitIndex);
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName() + "(" + insertFieldStr + ") VALUES");
            bw.newLine();
            bw.write("\t\t<foreach collection=\"list\" item=\"item\" separator=\",\">");
            bw.newLine();
            StringBuilder insertPropertyBuilder = new StringBuilder();
            StringBuilder insertOrUpdateBuilder = new StringBuilder();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (fieldInfo.getAutoIncrement()) {
                    continue;
                }
                insertPropertyBuilder.append("#{item.").append(fieldInfo.getPropertyName()).append("},");
                insertOrUpdateBuilder.append(fieldInfo.getFieldName()).append(" = VALUES(").append(fieldInfo.getFieldName()).append("),");
            }
            SplitIndex = insertPropertyBuilder.lastIndexOf(",") == -1 ? 0 : insertPropertyBuilder.lastIndexOf(",");
            String insertPropertyStr = insertPropertyBuilder.substring(0, SplitIndex);
            bw.write("\t\t\t(" + insertPropertyStr + ")");
            bw.newLine();
            bw.write("\t\t</foreach>");
            bw.newLine();
            bw.write("\t</insert>");

            //  批量新增修改
            bw.newLine();
            bw.write("\t<!-- 批量新增修改 (批量插入)-->");
            bw.newLine();
            bw.write("\t<insert id=\"insertOrUpdateBatch\" parameterType=\"" + poClass + "\">");
            bw.newLine();
            bw.write("\t\tINSERT INTO " + tableInfo.getTableName() + "(" + insertFieldStr + ") VALUES");
            bw.newLine();
            bw.write("\t\t<foreach collection=\"list\" item=\"item\" separator=\",\">");
            bw.newLine();
            bw.write("\t\t\t(" + insertPropertyStr + ")");
            bw.newLine();
            bw.write("\t\t</foreach>");
            bw.newLine();
            bw.write("\t\ton DUPLICATE key update");
            SplitIndex = insertOrUpdateBuilder.lastIndexOf(",") == -1 ? 0 : insertOrUpdateBuilder.lastIndexOf(",");
            String insertOrUpdatePropertyStr = insertOrUpdateBuilder.substring(0, SplitIndex);
            bw.write(" " + insertOrUpdatePropertyStr);
            bw.newLine();
            bw.write("\t</insert>");
            bw.newLine();
            bw.write("\t<!--多条件修改-->");
            bw.newLine();
            bw.write("\t<update id=\"updateByParam\" parameterType=\"" + queryClass + "\">");
            bw.newLine();
            bw.write("\t\tUPDATE " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<set>");
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.newLine();
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + "#{bean." + fieldInfo.getPropertyName() + "}" + ",");
                bw.newLine();
                bw.write("\t\t\t</if>");
            }
            bw.newLine();
            bw.write("\t\t</set>");
            bw.write("<include refid=\""+QUERY_CONDITION+"\"/>");
            bw.newLine();
            bw.write("\t</update>");
            bw.newLine();
            bw.write("\t<!--多条件删除-->");
            bw.newLine();
            bw.write("\t<delete id=\"deleteByParam\">");
            bw.newLine();
            bw.write("\t\t delete from " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t <include refid=\""+QUERY_CONDITION+"\" />");
            bw.newLine();
            bw.write("\t</delete>");
            bw.newLine();
            //  根据index更新
            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                List<FieldInfo> keyFieldInfo = entry.getValue();
                int index = 0;
                StringBuilder methodName = new StringBuilder();
                StringBuilder methodParams = new StringBuilder();
                for (FieldInfo fieldInfo : keyFieldInfo) {
                    index++;
                    methodName.append(StringUtils.upperCaseFirstLetter(fieldInfo.getPropertyName()));
                    methodParams.append(fieldInfo.getFieldName() + " = #{" + fieldInfo.getPropertyName() + "}");
                    if (index < keyFieldInfo.size()) {
                        methodName.append("And");
                        methodParams.append(" and ");
                    }

                }
                bw.write(String.format("\t<!-- 根据%s查询-->", methodName));
                bw.newLine();
                bw.write("\t<select id=\"selectBy" + methodName + "\" resultMap=\"base_result_map\">");
                bw.newLine();
                bw.write("\t\tSELECT <include refid=\"base_column_list\"/> from " + tableInfo.getTableName() + " where " + methodParams);
                bw.newLine();
                bw.write("\t</select>");
                bw.newLine();
                bw.write(String.format("\t<!-- 根据%s更新-->", methodName));
                bw.newLine();
                bw.write("\t<update id=\"updateBy" + methodName + "\" parameterType=\"" + poClass + "\">");
                bw.newLine();
                bw.write("\t\tUPDATE " + tableInfo.getTableName());
                bw.newLine();
                bw.write("\t\t<set>");
                bw.newLine();
                for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                    if (keyTempMap.containsValue(fieldInfo.getFieldName())) {
                        continue;
                    }
                    bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                    bw.newLine();
                    bw.write("\t\t\t\t" + fieldInfo.getFieldName() + " = #{" + "bean." + fieldInfo.getPropertyName() + "},");
                    bw.newLine();
                    bw.write("\t\t\t</if>");
                    bw.newLine();
                }
                bw.write("\t\t</set>");
                bw.newLine();
                bw.write("\t\twhere " + methodParams);
                bw.newLine();
                bw.write("\t</update>");
                bw.newLine();

                bw.write(String.format("\t<!-- 根据%s删除-->", methodName));
                bw.newLine();
                bw.write("\t<delete id=\"deleteBy" + methodName + "\">");
                bw.newLine();
                bw.write("\t\tdelete from " + tableInfo.getTableName() + " where " + methodParams);
                bw.newLine();
                bw.write("\t</delete>");

                bw.newLine();
            }


            bw.newLine();
            bw.write("</mapper>");
            bw.flush();
        } catch (Exception e) {
            logger.error("创建MapperXML失败", e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    logger.error(e.toString());
                }
            }
            if (outw != null) {
                try {
                    outw.close();
                } catch (IOException e) {
                    logger.error(e.toString());
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.error(e.toString());
                }
            }
        }


    }
}
