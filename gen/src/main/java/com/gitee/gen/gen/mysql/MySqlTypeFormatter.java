package com.gitee.gen.gen.mysql;

import com.gitee.gen.gen.TypeFormatter;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author tanghc
 */
public class MySqlTypeFormatter implements TypeFormatter {

    @Override
    public boolean isBit(String columnType) {
        return contains(Collections.singletonList("bit"), columnType);
    }

    @Override
    public boolean isBoolean(String columnType) {
        return contains(Collections.singletonList("boolean"), columnType);
    }

    @Override
    public boolean isTinyint(String columnType) {
        return contains(Collections.singletonList("tinyint"), columnType);
    }

    @Override
    public boolean isSmallint(String columnType) {
        return contains(Collections.singletonList("smallint"), columnType);
    }

    @Override
    public boolean isInt(String columnType) {
        return !isLong(columnType) && contains(Arrays.asList("int", "integer"), columnType);
    }

    @Override
    public boolean isLong(String columnType) {
        return !isVarchar(columnType) && contains(Collections.singletonList("bigint"), columnType);
    }

    @Override
    public boolean isFloat(String columnType) {
        return contains(Collections.singletonList("float"), columnType);
    }

    @Override
    public boolean isDouble(String columnType) {
        return contains(Collections.singletonList("double"), columnType);
    }

    @Override
    public boolean isDecimal(String columnType) {
        return contains(Collections.singletonList("decimal"), columnType);
    }

    @Override
    public boolean isVarchar(String columnType) {
        return contains(Arrays.asList("CHAR", "VARCHAR", "TEXT"), columnType);
    }

    @Override
    public boolean isDatetime(String columnType) {
        return contains(Arrays.asList("TIME", "DATETIME", "TIMESTAMP"), columnType);
    }

    @Override
    public boolean isDate(String columnType) {
        return contains(Collections.singletonList("DATE"), columnType);
    }

    @Override
    public boolean isBlob(String columnType) {
        return contains(Collections.singletonList("blob"), columnType);
    }

    @Override
    public boolean isJsonb(String columnType) {
        return false;
    }
}
