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
import java.util.ArrayList;
import java.util.List;

/**
 * @projectName: workspace
 * @package: com.easyjava.builder
 * @className: BuildPO
 * @author: proanimer
 * @description:
 * @date: 2024/5/11 9:31
 */
public class BuildQuery {
    private static final Logger logger = LoggerFactory.getLogger(BuildQuery.class);

    public static void BuildGetSet(BufferedWriter bw,List<FieldInfo> fieldInfoList) {
        for (FieldInfo field : fieldInfoList) {
            String tempField = StringUtils.upperCaseFirstLetter(field.getPropertyName());
            try {
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
            } catch (Exception e) {
                logger.error("getter setter错误.{}", e.toString());
            }

        }
    }
    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_QUERY);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String className = tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY;
        File poFile = new File(folder, className + ".java");
        OutputStream outputStream = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;

        try {
            outputStream = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            bw = new BufferedWriter(outw);
            bw.write("package " + Constants.PACKAGE_QUERY + ";");
            bw.newLine();
            bw.newLine();
            bw.write("import java.io.Serializable;");
            bw.newLine();
            if (tableInfo.getHaveBigDecimal()) {
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
            }
            if (tableInfo.getHaveDate() || tableInfo.getHaveDateTime()) {
                bw.write("import java.util.Date;");
                bw.newLine();
                bw.write(Constants.BEAN_DATE_FORMAT_CLASS);
                bw.newLine();
                bw.write("import " + Constants.PACKAGE_ENUM + ".DateTimePatternEnum;");
                bw.newLine();
                bw.write("import " + Constants.PACKAGE_UTILS + ".DateUtils;");
                bw.newLine();
                bw.write(Constants.BEAN_DATE_PARSE_CLASS);
                bw.newLine();
            }

            bw.newLine();
            //构建类注释
            BuildComment.createClassComment(bw, tableInfo.getComment() + "查询对象");
            bw.write("public class " + className + " extends BaseQuery{");
            bw.newLine();
//            List<FieldInfo> extendList = new ArrayList<>();
            for (FieldInfo field : tableInfo.getFieldList()) {
                BuildComment.createFieldComment(bw, field.getComment());
                bw.write("\tprivate " + field.getJavaType() + " " + field.getPropertyName() + ";");
                bw.newLine();
                bw.newLine();
                //String类型的参数
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, field.getSqlType())) {
                    String propertyName = field.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_FUZZY;
                    bw.write("\tprivate " + field.getJavaType() + " " + propertyName + ";");
                    bw.newLine();
                    bw.newLine();

//                    FieldInfo fuzzyField = new FieldInfo();
//                    fuzzyField.setJavaType(field.getJavaType());
//                    fuzzyField.setPropertyName(propertyName);
//                    extendList.add(fuzzyField);
                }
                //Date和DateTime类型的参数
                if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, field.getSqlType()) || ArrayUtils.contains(Constants.SQL_DATE_TYPES, field.getSqlType())) {
                    bw.write("\tprivate String " + field.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_START + ";");
                    bw.newLine();
                    bw.newLine();
                    bw.write("\tprivate String " + field.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_END + ";");
                    bw.newLine();
                    bw.newLine();
//                    FieldInfo timeStartField = new FieldInfo();
//                    timeStartField.setJavaType("String");
//                    timeStartField.setPropertyName(field.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_START);
//                    extendList.add(timeStartField);
//
//                    FieldInfo timeEndField = new FieldInfo();
//                    timeEndField.setJavaType("String");
//                    timeEndField.setPropertyName(field.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_END);
//                    extendList.add(timeEndField);
                }
            }
            BuildGetSet(bw,tableInfo.getFieldList());
           BuildGetSet(bw,tableInfo.getFieldExtendList());

            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.error("创建PO失败{}", e.toString());
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
