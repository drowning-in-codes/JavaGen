package com.easyjava.builder;
/**
 * @Author: proanimer
 * @Description:
 * @Date: Created in 2024/5/21
 * @Modified By proanimer
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
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * @projectName: workspace
 * @package: com.easyjava.builder
 * @className: BuildService
 * @author: proanimer
 * @description:
 * @date: 2024/5/21 22:59
 */
public class BuildService {
    private static final Logger logger = LoggerFactory.getLogger(BuildService.class);

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_SERVICE);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String className = tableInfo.getBeanName() + "Service";
        File poFile = new File(folder, className + ".java");
        OutputStream outputStream = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;

        try {
            outputStream = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            bw = new BufferedWriter(outw);
            bw.write("package " + Constants.PACKAGE_SERVICE + ";");
            bw.newLine();

            bw.write("import java.util.List;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_VO + ".PaginationResultVO;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." + tableInfo.getBeanParamName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
            bw.newLine();
            bw.newLine();
            BuildComment.createClassComment(bw, tableInfo.getComment() + "Service");
            bw.write("public interface " + className + " {");
            bw.newLine();

            BuildComment.createMethodComment(bw, "根据条件查询列表");
            bw.write("\tList<" + tableInfo.getBeanName() + "> findListByParam(" + tableInfo.getBeanParamName() + " param);");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw, "根据条件查询数量");
            bw.write("\tint findCountByParam(" + tableInfo.getBeanParamName() + " param);");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw, "分页查询");
            bw.write("\tPaginationResultVO<" + tableInfo.getBeanName() + "> findListByPage(" + tableInfo.getBeanParamName() + " query);");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw, "新增");
            bw.write("\tint add(" + tableInfo.getBeanName() + " bean);");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw, "批量新增");
            bw.write("\tint addBatch(List<" + tableInfo.getBeanName() + "> listBean);");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw, "批量新增或修改");
            bw.write("\tint addOrUpdateBatch(List<" + tableInfo.getBeanName() + "> listBean);");
            bw.newLine();
            bw.newLine();

            for (Map.Entry<String, List<FieldInfo>> entry : tableInfo.getKeyIndexMap().entrySet()) {
                List<FieldInfo> keyFieldInfoList = entry.getValue();
                int index = 0;
                StringBuilder methodName = new StringBuilder();
                StringBuilder methodParams = new StringBuilder();

                for (FieldInfo fieldInfo : keyFieldInfoList) {
                    index++;
                    methodName.append(StringUtils.upperCaseFirstLetter(fieldInfo.getPropertyName()));
                    if (index < keyFieldInfoList.size()) {
                        methodName.append("And");
                    }
                    methodParams.append(fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName());
                    if (index < keyFieldInfoList.size()) {
                        methodParams.append(", ");
                    }
                }
                BuildComment.createFieldComment(bw, "根据" + methodName + "查询");
                bw.write("\t"+tableInfo.getBeanName()+" get"+tableInfo.getBeanName()+"By" + methodName + "(" + methodParams + ");");
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodName + "更新");
                bw.write("\tint update"+tableInfo.getBeanName()+"By" + methodName + "("+tableInfo.getBeanName()+" bean, " + methodParams + ");");
                bw.newLine();
                bw.newLine();
                BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
                bw.write("\tint delete"+tableInfo.getBeanName()+"By" + methodName + "("+ methodParams + ");");
                bw.newLine();
                bw.newLine();

            }


            bw.newLine();
            bw.write("}");
            bw.newLine();
            bw.flush();
        } catch (Exception e) {
            logger.error("创建Service失败", e);
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
