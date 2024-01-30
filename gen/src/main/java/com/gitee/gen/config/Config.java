package com.gitee.gen.config;

import com.gitee.gen.service.UpgradeService;
import com.gitee.gen.util.StringUtil;
import com.gitee.gen.util.SystemUtil;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.noear.solon.web.staticfiles.StaticMappings;
import org.noear.solon.web.staticfiles.repository.FileStaticRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

@Configuration
public class Config {

    static Logger log = LoggerFactory.getLogger(Config.class);

    @Inject
    UpgradeService upgradeService;

    //此下的 db1 与 mybatis.db1 将对应在起来 //可以用 @Db("db1") 注入mapper
    //typed=true，表示默认数据源。@Db 可不带名字注入
    @Bean(name = "db1", typed = true)
    public DataSource db1(@Inject("${gen.db1}") BasicDataSource ds) {
        return ds;
    }

    //调整 db1 的配置，或添加插件 (配置可以解决的，不需要这块代码)
    //@Bean
    //public void db1_cfg(@Db("db1") org.apache.ibatis.session.Configuration cfg) {
    //    cfg.setCacheEnabled(false);
    //}

    @Init
    public void start() {
        upgradeService.init();
        initStaticFile();
    }

    private static void initStaticFile() {
        String frontLocation = Solon.context().cfg().get("gen.front-location", "");
        String frontRoot;
        if (StringUtils.isNotBlank(frontLocation)) {
            frontRoot = StringUtil.trimTrailingCharacter(frontLocation, '/');
        } else {
            String homeDir = SystemUtil.getBinPath();
            if ("/".equals(homeDir)) {
                homeDir = "";
            }
            frontRoot = homeDir + "/dist";
        }
        log.info("前端资源目录：{}", frontRoot);

//        String frontLocation = "file:" + frontRoot;
        StaticMappings.add("/", new FileStaticRepository(frontRoot));
//        StaticMappings.add("/favicon.ico", new FileStaticRepository(frontRoot + "/favicon.ico"));
//        StaticMappings.add("/static/", new FileStaticRepository(frontRoot + "/static/"));
//        StaticMappings.add("/velocity/", new FileStaticRepository(frontRoot + "/velocity/"));

    }
}
