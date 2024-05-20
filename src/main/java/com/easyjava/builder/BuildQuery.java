package com.easyjava.builder;/**
 * @Author: proanimer
 * @Description:
 * @Date: Created in 2024/5/11
 * @Modified By
 */

import com.easyjava.beans.Constants;
import com.easyjava.beans.FieldInfo;
import com.easyjava.beans.TableInfo;
import com.easyjava.utils.DateUtils;
import com.easyjava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * @projectName: workspace
 * @package: com.easyjava.builder
 * @className: BuildPO
 * @author: proanimer
 * @description:
 * @date: 2024/5/11 9:31
 */
public class BuildPO {
    private static final Logger logger = LoggerFactory.getLogger(BuildPO.class);

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_PO);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File poFile = new File(folder, tableInfo.getBeanName() + ".java");
        OutputStream outputStream = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;

        try {
            outputStream = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            bw = new BufferedWriter(outw);
            bw.write("package " + Constants.PACKAGE_PO + ";");
            bw.newLine();
            bw.newLine();
            bw.write("import java.io.Serializable;");
            bw.newLine();
            boolean haveIgnoreBean = false;
            for (FieldInfo field : tableInfo.getFieldList()) {
                if (ArrayUtils.contains(Constants.IGNORE_BEAN_TOJSON_ID.split(","), field.getPropertyName())) {
                    haveIgnoreBean = true;
                    break;
                }
            }
            if (haveIgnoreBean) {
                bw.write(Constants.IGNORE_BEAN_TOJSON_CLASS);
                bw.newLine();
            }
            if (tableInfo.getHaveBigDecimal()) {
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
            }
            if (tableInfo.getHaveDate() || tableInfo.getHaveDateTime()) {
                bw.write("import java.util.Date;");
                bw.newLine();
                bw.write(Constants.BEAN_DATE_FORMAT_CLASS);
                bw.newLine();
                bw.write("import "+Constants.PACKAGE_ENUM+".DateTimePatternEnum;");
                bw.newLine();
                bw.write("import "+Constants.PACKAGE_UTILS+".DateUtils;");
                bw.newLine();
                bw.write(Constants.BEAN_DATE_PARSE_CLASS);
                bw.newLine();
            }

            bw.newLine();
            //构建类注释
            BuildComment.createClassComment(bw, tableInfo.getComment());
            bw.write("public class " + tableInfo.getBeanName() + " implements Serializable {");
            bw.newLine();
            for (FieldInfo field : tableInfo.getFieldList()) {
                BuildComment.createFieldComment(bw, field.getComment());
                if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, field.getSqlType())) {
                    bw.write("\t" + String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, DateUtils.YYYY_MM_DD_HH_MM_SS));
                    bw.newLine();

                    bw.write("\t" + String.format(Constants.BEAN_DATE_PARSE_EXPRESSION, DateUtils.YYYY_MM_DD_HH_MM_SS));
                    bw.newLine();
                }
                if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, field.getSqlType())) {
                    bw.write("\t" + String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, DateUtils.YYYY_MM_DD));
                    bw.newLine();

                    bw.write("\t" + String.format(Constants.BEAN_DATE_PARSE_EXPRESSION, DateUtils.YYYY_MM_DD));
                    bw.newLine();
                }
                if (ArrayUtils.contains(Constants.IGNORE_BEAN_TOJSON_ID.split(","), field.getPropertyName())) {
                    bw.write("\t" + Constants.IGNORE_BEAN_TOJSON_EXPRESSION);
                    bw.newLine();
                }

                bw.write("\tprivate " + field.getJavaType() + " " + field.getPropertyName() + ";");
                bw.newLine();
                bw.newLine();
            }
            for (FieldInfo field : tableInfo.getFieldList()) {
                String tempField = StringUtils.upperCaseFirstLetter(field.getPropertyName());
                bw.write("\tpublic void set" + tempField + "(" + field.getJavaType() + " " + field.getPropertyName() + ") {");
                bw.newLine();
                bw.write("\t\tthis." + field.getPropertyName() + " = " + field.getPropertyName() + ";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                bw.write("\tpublic " + field.getJavaType() + " get" + tempField + "() {");
                bw.newLine();
                bw.write("\t\treturn this." + field.getPropertyName() + ";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
            }
            StringBuffer fieldInfo = new StringBuffer();
            int index = 0;
            for (FieldInfo field : tableInfo.getFieldList()) {
                index++;
                String comment = field.getComment();
                if (comment.isEmpty()) {
                    comment = field.getPropertyName();
                }
                String properName = field.getPropertyName();
                if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, field.getSqlType())) {
                    properName = "DateUtils.format("+properName +",DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())";

                } else if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, field.getSqlType())) {
                    properName = "DateUtils.format("+properName +",DateTimePatternEnum.YYYY_MM_DD.getPattern())";
                }
                fieldInfo.append(comment).append(":\" + (").append(field.getPropertyName()).append(" == null ? \"空\" : ").append(properName).append(")");
                if (index<tableInfo.getFieldList().size()) {
                    fieldInfo.append(" + ");
                    fieldInfo.append("\",");
                }
            }
            String fieldStr = fieldInfo.toString();
            fieldStr = "\"" + fieldStr;
//            fieldStr = fieldStr.substring(0, fieldStr.lastIndexOf("+"));
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic String toString() {");
            bw.newLine();
            bw.write("\t\treturn " + fieldStr + ";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.error("创建PO失败", e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outw != null) {
                try {
                    outw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
