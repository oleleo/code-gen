package com.gitee.gen.config;

import com.gitee.gen.gen.DataSourceManager;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

/**
 * @author thc
 */
public class ConnectionHandler implements Handler {
    @Override
    public void handle(Context ctx) throws Throwable {
        DataSourceManager.closeConnection();
    }
}
