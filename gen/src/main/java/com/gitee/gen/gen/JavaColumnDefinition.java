package com.gitee.gen.gen;

import com.gitee.gen.gen.converter.ColumnTypeConverter;
import com.gitee.gen.gen.converter.JavaColumnTypeConverter;
import com.gitee.gen.util.FieldUtil;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tanghc
 */
public class JavaColumnDefinition extends ColumnDefinition {

    private static final JavaColumnTypeConverter COLUMN_TYPE_CONVERTER = new JavaColumnTypeConverter();

    private static final Map<String, String> TYPE_MYBATIS_MAP = new HashMap<>(64);

    static {
        TYPE_MYBATIS_MAP.put(TypeEnum.BIT.getType(), "BOOLEAN");
        TYPE_MYBATIS_MAP.put(TypeEnum.BOOLEAN.getType(), "BOOLEAN");
        TYPE_MYBATIS_MAP.put(TypeEnum.TINYINT.getType(), "TINYINT");
        TYPE_MYBATIS_MAP.put(TypeEnum.SMALLINT.getType(), "INTEGER");
        TYPE_MYBATIS_MAP.put(TypeEnum.INT.getType(), "INTEGER");
        TYPE_MYBATIS_MAP.put(TypeEnum.BIGINT.getType(), "BIGINT");
        TYPE_MYBATIS_MAP.put(TypeEnum.FLOAT.getType(), "FLOAT");
        TYPE_MYBATIS_MAP.put(TypeEnum.DOUBLE.getType(), "DOUBLE");
        TYPE_MYBATIS_MAP.put(TypeEnum.DECIMAL.getType(), "DECIMAL");
        TYPE_MYBATIS_MAP.put(TypeEnum.VARCHAR.getType(), "VARCHAR");
        TYPE_MYBATIS_MAP.put(TypeEnum.DATETIME.getType(), "TIMESTAMP");
        TYPE_MYBATIS_MAP.put(TypeEnum.BLOB.getType(), "BLOB");
    }

    public String getMybatisJdbcType() {
        return TYPE_MYBATIS_MAP.getOrDefault(getType(), "VARCHAR");
    }

    /**
     * 返回java字段名,并且第一个字母大写
     *
     * @return 返回字段名
     */
    public String getJavaFieldNameUF() {
        return FieldUtil.upperFirstLetter(getJavaFieldName());
    }

    /**
     * 返回java字段
     *
     * @return 返回java字段
     */
    public String getJavaFieldName() {
        String fieldName = FieldUtil.underlineFilter(getColumnName());
        if(StringUtils.isEmpty(fieldName)){
            return fieldName;
        }
        fieldName = fieldName.replaceAll("_", "");
        return fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
    }

    /**
     * 获得基本类型,int,float
     *
     * @return 返回基本类型
     */

    public String getJavaType() {
        return getFieldType();
    }

    /**
     * 获得装箱类型,Integer,Float
     *
     * @return 返回装箱类型
     */

    public String getJavaTypeBox() {
        return getFieldTypeBox();
    }

    @Override
    public ColumnTypeConverter getColumnTypeConverter() {
        return COLUMN_TYPE_CONVERTER;
    }
}
