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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @projectName: workspace
 * @package: com.easyjava.builder
 * @className: BuildMapper
 * @author: proanimer
 * @description:
 * @date: 2024/5/18 11:33
 */
public class BuildMapper {
    private static final Logger logger = LoggerFactory.getLogger(BuildMapper.class);

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_MAPPER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String className = tableInfo.getBeanName() + Constants.SUFFIX_MAPPERS;
        File poFile = new File(folder, className + ".java");
        OutputStream outputStream = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;

        try {
            outputStream = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            bw = new BufferedWriter(outw);
            bw.write("package " + Constants.PACKAGE_MAPPER + ";");
            bw.newLine();
            bw.newLine();
            bw.write("import org.apache.ibatis.annotations.Param;");
            bw.newLine();
            bw.newLine();

            //构建类注释
            BuildComment.createClassComment(bw, tableInfo.getComment() + "Mapper");
            bw.write("public interface " + className + "<T, P> extends BaseMapper<T, P> {");
            bw.newLine();
            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();


            for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                List<FieldInfo> keyFieldInfo = entry.getValue();
                int index = 0;
                StringBuilder methodName = new StringBuilder();
                StringBuilder methodParams = new StringBuilder();

                for (FieldInfo fieldInfo : keyFieldInfo) {
                    index++;
                    methodName.append(StringUtils.upperCaseFirstLetter(fieldInfo.getPropertyName()));
                    if (index < keyFieldInfo.size()) {
                        methodName.append("And");
                    }
                    methodParams.append("@Param(\"").append(fieldInfo.getPropertyName()).append("\") ").append(fieldInfo.getJavaType()).append(" ").append(fieldInfo.getPropertyName());
                    if (index < keyFieldInfo.size()) {
                        methodParams.append(", ");
                    }
                }
                bw.newLine();
                BuildComment.createMethodComment(bw, "根据" + methodName + "查询");
                bw.write("\tT selectBy" + methodName + "(" + methodParams + ");");
                bw.newLine();
                bw.newLine();

                BuildComment.createMethodComment(bw, "根据" + methodName + "更新");
                bw.write("\tInteger updateBy" + methodName + "(@Param(\"bean\") T t, " + methodParams + ");");
                bw.newLine();
                bw.newLine();

                BuildComment.createMethodComment(bw, "根据" + methodName + "删除");
                bw.write("\tInteger deleteBy" + methodName + "(" + methodParams + ");");
                bw.newLine();
            }
//            BuildComment.createMethodComment(bw, "多条件修改");
//            bw.write("\tInteger updateByParam(@Param(\"bean\") T t, @Param(\"query\") P p);");
//            bw.newLine();
//
//            BuildComment.createMethodComment(bw, "多条件删除");
//            bw.write("\tInteger deleteByParam(@Param(\"query\") P p);");
//            bw.newLine();
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.error("创建Mapper失败", e);
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
