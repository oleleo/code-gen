package com.gitee.gen.config;

import com.gitee.gen.common.Action;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.route.RouterInterceptorChain;
import org.noear.solon.validation.ValidatorException;

@Component(index = 0) //index 为顺序位（不加，则默认为0）
public class AppRouterInterceptor implements RouterInterceptor {
    @Override
    public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        try {
            chain.doIntercept(ctx, mainHandler);

            if (mainHandler == null) {
                ctx.render(Action.err("资源不存在"));
            }
        } catch (ValidatorException e) {
            ctx.render(Action.err(e.getMessage())); //e.getResult().getDescription()
        } catch (Throwable e) {
            ctx.render(Action.err(e.getMessage()));
        }
    }
}
