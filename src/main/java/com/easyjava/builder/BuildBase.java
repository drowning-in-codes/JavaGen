package com.easyjava.builder;
/**
 * @Author: proanimer
 * @Description:
 * @Date: Created in 2024/5/12
 * @Modified By proanimer
 */

import com.easyjava.beans.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @projectName: workspace
 * @package: com.easyjava.builder
 * @className: BuildBase
 * @author: proanimer
 * @description:
 * @date: 2024/5/12 11:39
 */
public class BuildBase {
    private static Logger logger = LoggerFactory.getLogger(BuildBase.class);

    public static void execute() {
        List<String> headerInfoList = new ArrayList<String>();
        headerInfoList.add("package " + Constants.PACKAGE_UTILS);
        build(headerInfoList, "DateUtils", Constants.PATH_UTILS);

        headerInfoList.clear();
        headerInfoList.add("import org.apache.commons.lang3.StringUtils");
        headerInfoList.add("package " + Constants.PACKAGE_UTILS);
        build(headerInfoList, "StringUtils", Constants.PATH_UTILS);

        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_ENUM);
        build(headerInfoList, "DateTimePatternEnum", Constants.PATH_ENUM);

        // 生成baseMapper
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_MAPPER);
        build(headerInfoList, "BaseMapper", Constants.PATH_MAPPER);

        //生成pagesize枚举
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_ENUM);
        build(headerInfoList, "PageSize", Constants.PATH_ENUM);

        //生成SimpePage枚举
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_QUERY);
        headerInfoList.add("import " + Constants.PACKAGE_ENUM + ".PageSize");
        build(headerInfoList, "SimplePage", Constants.PATH_QUERY);

        //生成BaseQuery枚举
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_QUERY);
        build(headerInfoList, "BaseQuery", Constants.PATH_QUERY);

        //生成PaginationResultVO
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_VO);
        headerInfoList.add("import java.util.ArrayList");
        headerInfoList.add("import java.util.List");
        build(headerInfoList, "PaginationResultVO", Constants.PATH_VO);

        //生成Exception
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_EXCEPTION);
        headerInfoList.add("import " + Constants.PACKAGE_ENUM + ".ResponseCodeEnum;");
        build(headerInfoList, "BusinessException", Constants.PATH_EXCEPTION);

        //生成ResponseVO
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_VO);
        build(headerInfoList, "ResponseVO", Constants.PATH_VO);

        //生成ResponseCodeEnum
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_ENUM);
        build(headerInfoList, "ResponseCodeEnum", Constants.PATH_ENUM);

        //生成ABaseController
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_CONTROLLER);
        headerInfoList.add("import " + Constants.PACKAGE_ENUM + ".ResponseCodeEnum");
        headerInfoList.add("import " + Constants.PACKAGE_VO + ".ResponseVO");
        build(headerInfoList, "ABaseController", Constants.PATH_CONTROLLER);

        //生成AGlobalExceptionController
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_CONTROLLER);
        headerInfoList.add("import " + Constants.PACKAGE_ENUM + ".ResponseCodeEnum");
        headerInfoList.add("import " + Constants.PACKAGE_VO + ".ResponseVO");
        headerInfoList.add("import " + Constants.PACKAGE_EXCEPTION + ".BusinessException");
        headerInfoList.add("import org.slf4j.Logger");
        headerInfoList.add("import org.slf4j.LoggerFactory");
        headerInfoList.add("import org.springframework.dao.DuplicateKeyException");
        headerInfoList.add("import org.springframework.web.bind.annotation.RestControllerAdvice");
        headerInfoList.add("import org.springframework.web.bind.annotation.ExceptionHandler");
        headerInfoList.add("import org.springframework.web.servlet.NoHandlerFoundException");
        headerInfoList.add("import javax.servlet.http.HttpServletRequest");
        headerInfoList.add("import java.net.BindException");
        build(headerInfoList, "AGlobalExceptionHandlerController", Constants.PATH_CONTROLLER);

        //生成404静态页面
        if (Constants.DEAL_STATIC_404) {
            headerInfoList.clear();
            buildHtml(headerInfoList, "404", Constants.PATH_ERROR_STATIC);
        }


    }

    private static void build(List<String> headerInfoList, String filename, String outputPath) {
        File folder = new File(outputPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File javaFile = new File(outputPath, filename + ".java");
        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        InputStream in = null;
        InputStreamReader inr = null;
        BufferedReader br = null;
        try {
            out = new FileOutputStream(javaFile);
            outw = new OutputStreamWriter(out, StandardCharsets.UTF_8);
            bw = new BufferedWriter(outw);

            String templatePath = Objects.requireNonNull(BuildBase.class.getClassLoader().getResource("template/" + filename + ".txt")).getPath();
            in = new FileInputStream(templatePath);
            inr = new InputStreamReader(in, StandardCharsets.UTF_8);
            br = new BufferedReader(inr);
            for (String header : headerInfoList) {
                bw.write(header + ";");
                bw.newLine();
                if (header.contains("package")) {
                    bw.newLine();
                }
            }
            bw.newLine();
            String lineInfo = null;
            while ((lineInfo = br.readLine()) != null) {
                bw.write(lineInfo);
                bw.newLine();
            }
            bw.flush();
        } catch (Exception e) {
            logger.error("生成文件失败:{}", e.toString());
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (outw != null) {
                    outw.close();
                }
                if (out != null) {
                    out.close();
                }
                if (br != null) {
                    br.close();
                }
                if (inr != null) {
                    inr.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                logger.error("关闭流失败", e);
            }
        }


    }

    private static void buildHtml(List<String> headerInfoList, String filename, String outputPath) {
        File folder = new File(outputPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File javaFile = new File(outputPath, filename + ".html");
        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        InputStream in = null;
        InputStreamReader inr = null;
        BufferedReader br = null;
        try {
            out = new FileOutputStream(javaFile);
            outw = new OutputStreamWriter(out, StandardCharsets.UTF_8);
            bw = new BufferedWriter(outw);

            String templatePath = Objects.requireNonNull(BuildBase.class.getClassLoader().getResource("template/" + filename + ".txt")).getPath();
            in = new FileInputStream(templatePath);
            inr = new InputStreamReader(in, StandardCharsets.UTF_8);
            br = new BufferedReader(inr);
            for (String header : headerInfoList) {
                bw.write(header + ";");
                bw.newLine();
                if (header.contains("package")) {
                    bw.newLine();
                }
            }
            bw.newLine();
            String lineInfo = null;
            while ((lineInfo = br.readLine()) != null) {
                bw.write(lineInfo);
                bw.newLine();
            }
            bw.flush();
        } catch (Exception e) {
            logger.error("生成文件失败:{}", e.toString());
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (outw != null) {
                    outw.close();
                }
                if (out != null) {
                    out.close();
                }
                if (br != null) {
                    br.close();
                }
                if (inr != null) {
                    inr.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                logger.error("关闭流失败", e);
            }
        }


    }


}
