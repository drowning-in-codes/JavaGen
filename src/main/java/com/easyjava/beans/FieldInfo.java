package com.easyjava.beans;/**
 * @Author: proanimer
 * @Description:
 * @Date: Created in 2024/5/10
 * @Modified By
 */

/**
 * @projectName: workspace
 * @package: com.easyjava.beans
 * @className: FieldInfo
 * @author: proanimer
 * @description:
 * @date: 2024/5/10 14:16
 */
public class FieldInfo {

    private String FieldName;

    private String sqlType;

    private String propertyName;

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    private String javaType;

    private String comment;

    private Boolean isAutoIncrement;

    public Boolean getAutoIncrement() {
        return isAutoIncrement;
    }

    public void setAutoIncrement(Boolean autoIncrement) {
        isAutoIncrement = autoIncrement;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }


    public String getFieldName() {
        return FieldName;
    }

    public void setFieldName(String fieldName) {
        FieldName = fieldName;
    }
}
