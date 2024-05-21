package com.easyjava.builder;

import com.easyjava.beans.Constants;
import com.easyjava.utils.DateUtils;
import com.easyjava.utils.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * @Author: proanimer
 * @Description:
 * @Date: Created in 2024/5/11
 * @Modified By
 */


public class BuildComment {
    private static final Logger log = LoggerFactory.getLogger(BuildComment.class);

    public static void createClassComment(BufferedWriter bw, String classComment) throws Exception {
            bw.write("/**");
            bw.newLine();
            bw.write(" * @Author:"+ (Objects.equals(Constants.AUTHOR_COMMENT,null)?"anonymous":Constants.AUTHOR_COMMENT));
            bw.newLine();
            bw.write(" * @Description:"+classComment);
            bw.newLine();
            bw.write(" * @Date:" + DateUtils.format(new Date(), DateUtils._YYYYMMDD));
            bw.newLine();
            bw.write(" */");
            bw.newLine();

    }
    /**
     * @param bw:
     * @param fieldComment:
     * @return void
     * @author proanimer
     * @description TODO
     * @date 2024/5/11 20:59
     */
    public static void createFieldComment(BufferedWriter bw, String fieldComment) throws Exception {
        bw.write("\t/**");
        bw.newLine();
        bw.write("\t * @Description:" + (Objects.equals(fieldComment,null)?"":fieldComment));
        bw.newLine();
        bw.write("\t */");
        bw.newLine();


    }
    public static void createMethodComment(BufferedWriter bw, String methodComment) throws Exception {
            bw.write("\t/**");
            bw.newLine();
            bw.write("\t * @Description:"+methodComment);
            bw.newLine();
            bw.write("\t * @Date:" + DateUtils.format(new Date(), DateUtils._YYYYMMDD));
            bw.newLine();
            bw.write("\t */");
            bw.newLine();


    }
}

