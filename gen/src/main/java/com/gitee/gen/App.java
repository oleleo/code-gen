package com.gitee.gen;

import com.gitee.gen.config.ConnectionHandler;
import org.noear.solon.Solon;
import org.noear.solon.web.cors.CrossFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);


    public static void main(String[] args) {
        Solon.start(App.class, args, app->{
            //例：增加全局处理（用过滤器模式）//对静态资源亦有效
            app.filter(-1, new CrossFilter().allowedOrigins("*")); //加-1 优先级更高

            app.after(new ConnectionHandler());
        });
    }



}
