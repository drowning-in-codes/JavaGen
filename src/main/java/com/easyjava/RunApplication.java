package com.easyjava;
/**
 * @Author: proanimer
 * @Description:
 * @Date: Created in 2024/5/8
 * @Modified By
 */

import com.easyjava.beans.TableInfo;
import com.easyjava.builder.*;
import com.mysql.cj.xdevapi.Table;

import java.util.List;

/**
 * @projectName: workspace
 * @package: com.easyjava
 * @className: RunApplication
 * @author: proanimer
 * @description:
 * @date: 2024/5/8 22:33
 */
public class RunApplication {
    public static void main(String[] args) {
        List<TableInfo> tableInfoList =  BuildTable.getTables();
        BuildBase.execute();
        for (TableInfo tableInfo : tableInfoList) {
            BuildPO.execute(tableInfo);
            BuildQuery.execute(tableInfo);
            BuildMapper.execute(tableInfo);
            BuildMapperXml.execute(tableInfo);
            BuildService.execute(tableInfo);
            BuildServiceImpl.execute(tableInfo);
            BuildController.execute(tableInfo);
        }
    }
}
