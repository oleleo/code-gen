package com.gitee.gen.util;

/**
 * @author tanghc
 */
public class SystemUtil {

    /**
     * 获取程序执行目录，即jar包所在的目录。此方法只在部署后有用，开发模式下，这里返回target路径
     * @return 返回路径
     */
    public static String getBinPath() {
        return System.getProperty("user.dir");
    }

    public static String getUserHome() {
        return System.getProperty("user.home");
    }
}
