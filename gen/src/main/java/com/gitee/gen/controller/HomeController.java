package com.gitee.gen.controller;


import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

@Controller
public class HomeController {


    @Get
    @Mapping("/")
    public void index(Context context) {
        context.redirect("index.html");
    }

}
