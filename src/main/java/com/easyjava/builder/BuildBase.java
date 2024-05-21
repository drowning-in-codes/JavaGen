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
        headerInfoList.add("import " + Constants.PACKAGE_ENUM+".PageSize");
        build(headerInfoList, "SimplePage", Constants.PATH_QUERY);

        //生成BaseQuery枚举
        headerInfoList.clear();
        headerInfoList.add("package " + Constants.PACKAGE_QUERY);
        build(headerInfoList, "BaseQuery", Constants.PATH_QUERY);


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
