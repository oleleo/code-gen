package com.gitee.gen;

import com.gitee.gen.gen.ColumnDefinition;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author thc
 */
public class ColumnTest {

    @Test
    public void testType() {
        ColumnDefinition columnDefinition = new ColumnDefinition();
        columnDefinition.setColumnName("user_age");

        Assert.assertEquals("userAge", columnDefinition.getFieldNameCamel());
        Assert.assertEquals("UserAge", columnDefinition.getFieldNamePascal());
        Assert.assertEquals("user_age", columnDefinition.getFieldNameSnake());
        Assert.assertEquals("USER_AGE", columnDefinition.getFieldNameSnakeBig());
        Assert.assertEquals("User_Age", columnDefinition.getFieldNamePascalSnake());
        Assert.assertEquals("user-age", columnDefinition.getFieldNameKebab());
        Assert.assertEquals("USER-AGE", columnDefinition.getFieldNameKebabBig());
        Assert.assertEquals("User-Age", columnDefinition.getFieldNamePascalKebab());
    }

}
