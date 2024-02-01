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

        Assert.assertEquals("userAge", columnDefinition.getNameCamel());
        Assert.assertEquals("UserAge", columnDefinition.getNamePascal());
        Assert.assertEquals("user_age", columnDefinition.getNameSnake());
        Assert.assertEquals("USER_AGE", columnDefinition.getNameSnakeBig());
        Assert.assertEquals("User_Age", columnDefinition.getNamePascalSnake());
        Assert.assertEquals("user-age", columnDefinition.getNameKebab());
        Assert.assertEquals("USER-AGE", columnDefinition.getNameKebabBig());
        Assert.assertEquals("User-Age", columnDefinition.getNamePascalKebab());
    }

}
