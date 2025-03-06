package com.easyjava.builder;
/**
 * @Author: proanimer
 * @Description:
 * @Date: Created in 2024/5/22
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
 * @className: BuildServiceImpl
 * @author: proanimer
 * @description:
 * @date: 2024/5/22 15:57
 */
public class BuildServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(BuildServiceImpl.class);

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_SERVICE_IMPL);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String interfaceName = tableInfo.getBeanName() + "Service";
        String className = tableInfo.getBeanName() + "ServiceImpl";
        File poFile = new File(folder, className + ".java");
        OutputStream outputStream = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        try {
            outputStream = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            bw = new BufferedWriter(outw);
            bw.write("package " + Constants.PACKAGE_SERVICE_IMPL + ";");
            bw.newLine();
            String mapperName = tableInfo.getBeanName() + Constants.SUFFIX_MAPPERS;

            bw.write("import java.util.List;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_VO + ".PaginationResultVO;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." + tableInfo.getBeanParamName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
            bw.newLine();
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_SERVICE + "." + interfaceName + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_MAPPER + "." + mapperName + ";");
            bw.newLine();
            bw.write("import org.springframework.stereotype.Service;");
            bw.newLine();
            bw.write("import javax.annotation.Resource;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." + "SimplePage;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_ENUM + "." + "PageSize;");

            bw.newLine();
            bw.newLine();

            BuildComment.createClassComment(bw, className);
            bw.write("@Service(\"" + StringUtils.lowerCaseFirstLetter(interfaceName) + "\")");
            bw.newLine();
            bw.write("public class " + className + " implements " + interfaceName + " {");
            bw.newLine();
            bw.newLine();
            String mapperBeanName = StringUtils.lowerCaseFirstLetter(mapperName);
            bw.write("\t@Resource");
            bw.newLine();
            bw.write("\tprivate " + mapperName + "<" + tableInfo.getBeanName() + ", " + tableInfo.getBeanParamName() + "> " + mapperBeanName + ";");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw, "根据条件查询列表");
            bw.write("\tpublic List<" + tableInfo.getBeanName() + "> findListByParam(" + tableInfo.getBeanParamName() + " query) {");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".selectList(query);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw, "根据条件查询数量");
            bw.write("\tpublic int findCountByParam(" + tableInfo.getBeanParamName() + " query) {");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".selectCount(query);");
            bw.newLine();

            bw.write("\t}");
            bw.newLine();

            bw.newLine();
            BuildComment.createMethodComment(bw, "分页查询");
            bw.write("\tpublic PaginationResultVO<" + tableInfo.getBeanName() + "> findListByPage(" + tableInfo.getBeanParamName() + " query) {");
            bw.newLine();
            bw.write("\t\tint count = this.findCountByParam(query);");
            bw.newLine();
            bw.write("\t\tint pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();");
            bw.newLine();
            bw.write("\t\tSimplePage page = new SimplePage(query.getPageNo(), count, pageSize);");
            bw.newLine();
            bw.write("\t\tquery.setSimplePage(page);");
            bw.newLine();
            bw.write("\t\tList<" + tableInfo.getBeanName() + "> list = " + "this.findListByParam(query);");
            bw.newLine();
            bw.write("\t\tPaginationResultVO<" + tableInfo.getBeanName() + ">" + " result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);");
            bw.newLine();
            bw.write("\t\treturn result;");
            bw.newLine();
            bw.write("\t}");

            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw, "新增");
            bw.write("\tpublic int add(" + tableInfo.getBeanName() + " bean) {");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".insert(bean);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();
            BuildComment.createMethodComment(bw, "批量新增");
            bw.write("\tpublic int addBatch(List<" + tableInfo.getBeanName() + "> listBean) {");
            bw.newLine();
            bw.write("\t\tif (listBean == null || listBean.isEmpty()) {");
            bw.newLine();
            bw.write("\t\t\treturn 0;");
            bw.newLine();
            bw.write("\t\t}");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".insertBatch(listBean);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createMethodComment(bw, "批量新增或修改");
            bw.write("\tpublic int addOrUpdateBatch(List<" + tableInfo.getBeanName() + "> listBean) {");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".insertOrUpdateBatch(listBean);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            BuildComment.createMethodComment(bw, "多条件修改");
            bw.write("\tpublic int updateByParam(" + tableInfo.getBeanName() + " bean, "+tableInfo.getBeanParamName()+" query) {");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".updateByParam(bean,query);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            BuildComment.createMethodComment(bw, "多条件删除");
            bw.write("\tpublic int deleteByParam(" +tableInfo.getBeanParamName()+" query) {");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".deleteByParam(query);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            for (Map.Entry<String, List<FieldInfo>> entry : tableInfo.getKeyIndexMap().entrySet()) {
                List<FieldInfo> keyFieldInfoList = entry.getValue();
                int index = 0;
                StringBuilder methodName = new StringBuilder();
                StringBuilder methodParams = new StringBuilder();
                StringBuilder paramsBuilder = new StringBuilder();
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
                    paramsBuilder.append(fieldInfo.getPropertyName());
                    if (index < keyFieldInfoList.size()) {
                        paramsBuilder.append(", ");
                    }

                }
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodName + "查询");
                bw.write("\tpublic " + tableInfo.getBeanName() + " get" + tableInfo.getBeanName() + "By" + methodName + "(" + methodParams + ") {");
                bw.newLine();
                bw.write("\t\treturn this." + mapperBeanName + ".selectBy" + methodName + "(" + paramsBuilder + ");");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodName + "更新");
                bw.write("\tpublic int update" + tableInfo.getBeanName() + "By" + methodName + "(" + tableInfo.getBeanName() + " bean, " + methodParams + ") {");
                bw.newLine();
                bw.write("\t\treturn this." + mapperBeanName + ".updateBy" + methodName + "(bean, " + paramsBuilder + ");");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
                bw.write("\tpublic int delete" + tableInfo.getBeanName() + "By" + methodName + "(" + methodParams + ") {");
                bw.newLine();
                bw.write("\t\treturn this." + mapperBeanName + ".deleteBy" + methodName + "(" + paramsBuilder + ");");
                bw.newLine();
                bw.write("\t}");
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
