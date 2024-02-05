package com.gitee.gen.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author tanghc
 */
public class SystemUtil {

    private static volatile String binPath;

    /**
     * 获取程序执行目录，即jar包所在的目录。此方法只在部署后有用，开发模式下，这里返回target路径
     *
     * @return 返回路径
     */
    public static String getBinPath() {
        if (binPath == null) {
            synchronized (SystemUtil.class) {
                if (binPath == null) {
                    String path = SystemUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                    try {
                        path = URLDecoder.decode(path, "UTF-8");
                        path = StringUtil.trimTrailingCharacter(path, '/');
                        int index = path.lastIndexOf('/');
                        path = path.substring(0, index);
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    binPath = path;
                }
            }
        }
        return binPath;
    }

    public static String getUserHome() {
        return System.getProperty("user.home");
    }
}
