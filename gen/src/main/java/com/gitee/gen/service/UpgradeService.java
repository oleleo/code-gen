package com.gitee.gen.service;

import com.gitee.gen.App;
import com.gitee.gen.entity.ColumnInfo;
import com.gitee.gen.mapper.UpgradeMapper;
import com.gitee.gen.util.SystemUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.AppClassLoader;
import org.noear.solon.core.util.IoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/**
 * 升级
 *
 * @author tanghc
 */
@Component
public class UpgradeService {

    private static final Logger log = LoggerFactory.getLogger(UpgradeService.class);

    public static final String TABLE_DATASOURCE_CONFIG = "datasource_config";
    public static final String TABLE_TEMPLATE_CONFIG = "template_config";
    public static final String TABLE_TEMPLATE_GROUP = "template_group";
    public static final String TABLE_GENERATE_HISTORY = "generate_history";

    // 版本号，格式：xx.yy.zz，如：1.16.0,1.16.11
    private static final int CURRENT_VERSION = 200000;

    private static int lastVersion;

    @Db
    private UpgradeMapper upgradeMapper;

    @Inject("${gen.db1.driverClassName}")
    private String driverClassName;

    @Inject("${gen.db-name:gen}")
    private String dbName;

    @Inject("${gen.db-file-name:gen.db}")
    private String dbFileName;

    public void init() {
        initDatabase();
        lastVersion = getLocalVersion();
        upgrade();
    }

    private void initDatabase() {
        File dbFile = getDbFile();
        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try (InputStream inputStream = App.class.getClassLoader().getResourceAsStream("gen_init.db")) {
                Path path = dbFile.toPath();
                log.info("初始化数据库文件, path={}", path);
                OutputStream outputStream = Files.newOutputStream(path);
                IoUtil.transferTo(inputStream, outputStream);
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException("初始化数据库失败", e);
            }
        }
    }

    private File getDbFile() {
        String currentPath = SystemUtil.getBinPath();
        return new File(currentPath + "/" + dbFileName);
    }

    private static int getLocalVersion() {
        String filepath = getVersionFile();
        File versionFile = new File(filepath);
        if (versionFile.exists()) {
            try {
                InputStream is = Files.newInputStream(Paths.get(filepath));
                String val = IoUtil.transferToString(is);
                return Integer.parseInt(val);
            } catch (IOException e) {
                log.error("read 'version.txt' error", e);
            }
        }
        return 0;
    }

    private static void writeNewVersion() {
        String filepath = getVersionFile();
        try {
            OutputStream out = Files.newOutputStream(Paths.get(filepath));
            byte[] bytes = String.valueOf(CURRENT_VERSION).getBytes(StandardCharsets.UTF_8);
            out.write(bytes); //  write(byte[] b)
            out.close();
        } catch (IOException e) {
            throw new RuntimeException("初始version失败", e);
        }
    }

    private static String getVersionFile() {
        String filename = "version.txt";
       return SystemUtil.getBinPath() + "/" + filename;
    }

    /**
     * 升级
     */
    public void upgrade() {
        if (lastVersion == 0) {
            upgradeV1_4_0();
            upgradeV1_4_12();
            upgradeV1_4_17();
            upgradeV1_5_2();
        }
        doUpgrade();
        writeNewVersion();
    }

    private void doUpgrade() {
        upgradeV1_6_0();
    }

    private void upgradeV1_6_0() {
        if (lastVersion < 101600) {
            this.addColumn(TABLE_DATASOURCE_CONFIG, "db_desc", "varchar(64)");
            this.addColumn(TABLE_DATASOURCE_CONFIG, "db_group_name", "varchar(64)");
        }
    }

    private void upgradeV1_4_17() {
        this.addColumn(TABLE_TEMPLATE_CONFIG, "folder", "varchar(64)");
    }

    private void upgradeV1_4_12() {
        this.addColumn(TABLE_DATASOURCE_CONFIG, "schema_name", "varchar(100)");
    }

    /**
     * 升级v1.4.0
     */
    private void upgradeV1_4_0() {
        this.createTable(TABLE_GENERATE_HISTORY);
        boolean isCreate = this.createTable(TABLE_TEMPLATE_GROUP);
        if (isCreate) {
            runSql("INSERT INTO `template_group` (`id`, `group_name`, `is_deleted`) VALUES (1,'default',0)");
        }

        this.addColumn(TABLE_DATASOURCE_CONFIG, "package_name", "varchar(100)");
        this.addColumn(TABLE_DATASOURCE_CONFIG, "del_prefix", "varchar(200)");
        this.addColumn(TABLE_DATASOURCE_CONFIG, "group_id", "int");

        this.addColumn(TABLE_TEMPLATE_CONFIG, "group_id", "int");
        this.addColumn(TABLE_TEMPLATE_CONFIG, "group_name", "varchar(100)");
        runSql("update template_config set group_id=1,group_name='default' where group_id IS NULL");
    }

    /**
     * 升级v1.5.2
     * 1、前端：修复修改数据源时候不管什么数据库都带出oracle数据库
     * 2、前端：新增oracle类型数据库 数据库角色可以为空
     * 3、前端：修复数据库类型为oracle数据库时候测试连接服务器字段展示undefined问题
     * 4、前后端：新增author作者名配置，方便模板中插入作者
     * 5、后端：修复postgresql数据库表如果没有设置主键无法获取列数组问题
     */
    private void upgradeV1_5_2() {
        this.addColumn(TABLE_DATASOURCE_CONFIG, "author", "varchar(255)");
    }

    private void runSql(String sql) {
        upgradeMapper.runSql(sql);
    }

    /**
     * 添加表字段
     *
     * @param tableName  表名
     * @param columnName 字段名
     * @param type       字段类型，如：varchar(128),text,integer
     * @return 返回true，插入成功
     */
    public boolean addColumn(String tableName, String columnName, String type) {
        if (!isColumnExist(tableName, columnName)) {
            if (isMysql()) {
                upgradeMapper.addColumnMysql(tableName, columnName, type);
            } else if (isDm()) {
                upgradeMapper.addColumnDm(tableName, columnName, type);
            } else {
                upgradeMapper.addColumn(tableName, columnName, type);
            }
            return true;
        }
        return false;
    }

    /**
     * 创建表
     *
     * @param tableName 表名
     * @return 创建成功返回true
     */
    public boolean createTable(String tableName) {
        if (!isTableExist(tableName)) {
            String sql = this.loadDDL(tableName);
            upgradeMapper.runSql(sql);
            return true;
        }
        return false;
    }

    private String loadDDL(String tableName) {
        String tmp_dm = "ddl_%s_dm.txt";
        String tmp_mysql = "ddl_%s_mysql.txt";
        String tmp_sqlite = "ddl_%s_sqlite.txt";
        String tmp = isDm() ? tmp_dm : (isMysql() ? tmp_mysql : tmp_sqlite);
        String filename = "upgrade/" + String.format(tmp, tableName);
//        ClassPathResource resource = new ClassPathResource(filename);
        InputStream inputStream = AppClassLoader.getSystemResourceAsStream(filename);
        if (inputStream == null) {
            throw new RuntimeException("找不到文件：" + filename);
        }
        try {
            return IoUtil.transferToString(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("打开文件出错", e);
        }
    }

    /**
     * 判断列是否存在
     *
     * @param tableName  表名
     * @param columnName 列名
     * @return true：存在
     */
    public boolean isColumnExist(String tableName, String columnName) {
        List<ColumnInfo> columnInfoList = isDm() ? upgradeMapper.listColumnInfoDm(tableName) :
                (isMysql() ? upgradeMapper.listColumnInfoMysql(tableName, dbName) : upgradeMapper.listColumnInfo(tableName));
        return columnInfoList
                .stream()
                .anyMatch(columnInfo -> Objects.equals(columnInfo.getName(), columnName));
    }

    /**
     * 表是否存在
     *
     * @param tableName
     * @return
     */
    public boolean isTableExist(String tableName) {
        List<String> tableNameList;
        if (isMysql()) {
            tableNameList = upgradeMapper.listTableNameMysql();
        } else if (isDm()) {
            tableNameList = upgradeMapper.listTableNameDm();
        } else {
            tableNameList = upgradeMapper.listTableName();
        }
        return tableNameList != null && tableNameList.contains(tableName);
    }

    private boolean isMysql() {
        return this.driverClassName.contains("mysql");
    }

    private boolean isDm() {
        return this.driverClassName.contains("dm");
    }

}
