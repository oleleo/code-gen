package com.gitee.gen.service;

import com.gitee.gen.common.GeneratorParam;
import com.gitee.gen.entity.TemplateConfig;
import com.gitee.gen.gen.CodeFile;
import com.gitee.gen.gen.GeneratorConfig;
import com.gitee.gen.gen.SQLContext;
import com.gitee.gen.gen.SQLService;
import com.gitee.gen.gen.SQLServiceFactory;
import com.gitee.gen.gen.TableDefinition;
import com.gitee.gen.gen.TableSelector;
import com.gitee.gen.util.FormatUtil;
import com.gitee.gen.util.VelocityUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 生成代码逻辑
 */
@Component
public class GeneratorService {

    static ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Inject
    private TemplateConfigService templateConfigService;

    @Inject
    private GenerateHistoryService generateHistoryService;

    @Inject
    private TypeConfigService typeConfigService;

    @Inject("${gen.format-xml:false}")
    private String formatXml;


    /**
     * 生成代码内容,map的
     *
     * @param generatorParam 生成参数
     * @param generatorConfig 数据源配置
     * @return 一张表对应多个模板
     */
    public List<CodeFile> generate(GeneratorParam generatorParam, GeneratorConfig generatorConfig) {
        List<SQLContext> contextList = this.buildSQLContextList(generatorParam, generatorConfig);
        List<CodeFile> codeFileList = new ArrayList<>();

        for (SQLContext sqlContext : contextList) {
            setPackageName(sqlContext, generatorParam.getPackageName());
            setDelPrefix(sqlContext, generatorParam.getDelPrefix());
            setAuthor(sqlContext, generatorParam.getAuthor());
            for (int tcId : generatorParam.getTemplateConfigIdList()) {
                TemplateConfig template = templateConfigService.getById(tcId);
                String folder = template.getFolder();
                if (StringUtils.isEmpty(folder)) {
                    folder = template.getName();
                }else{
                    //文件目录可以使用变量
                    folder = doGenerator(sqlContext, folder);
                }

                setFolder(sqlContext, folder);

                //获取文件名
                String fileName = doGenerator(sqlContext, template.getFileName());
                String content = doGenerator(sqlContext, template.getContent());
                content = this.formatCode(fileName, content);
                CodeFile codeFile = new CodeFile();
                codeFile.setFolder(folder);
                codeFile.setFileName(fileName);
                codeFile.setContent(content);
                codeFileList.add(codeFile);
            }
        }

        executorService.execute(() -> {
            generateHistoryService.saveHistory(generatorParam);
        });

        return codeFileList;
    }

    // 格式化代码
    private String formatCode(String fileName, String content) {
        if (Objects.equals("true", formatXml) && fileName.endsWith(".xml")) {
            return FormatUtil.formatXml(content);
        }
        return content;
    }


    /**
     * 返回SQL上下文列表
     *
     * @param generatorParam 参数
     * @param generatorConfig 配置
     * @return 返回SQL上下文
     */
    private List<SQLContext> buildSQLContextList(GeneratorParam generatorParam, GeneratorConfig generatorConfig) {

        List<String> tableNames = generatorParam.getTableNames();
        List<SQLContext> contextList = new ArrayList<>();
        SQLService service = SQLServiceFactory.build(generatorConfig);

        TableSelector tableSelector = service.getTableSelector(generatorConfig);
        tableSelector.setSchTableNames(tableNames);
        tableSelector.setColumnTypeConverter(typeConfigService.buildColumnTypeConverter());

        List<TableDefinition> tableDefinitions = tableSelector.getTableDefinitions();

        for (TableDefinition tableDefinition : tableDefinitions) {
            SQLContext sqlContext = new SQLContext(tableDefinition);
            sqlContext.setDbName(generatorConfig.getDbName());
            contextList.add(sqlContext);
        }

        return contextList;
    }

    private void setPackageName(SQLContext sqlContext, String packageName) {
        if (StringUtils.isNotBlank(packageName)) {
            sqlContext.setPackageName(packageName);
        }
    }

    private void setFolder(SQLContext sqlContext, String folder) {
        if (StringUtils.isNotBlank(folder)) {
            sqlContext.setPackageSubPath(folder);
        }
    }

    private void setDelPrefix(SQLContext sqlContext, String delPrefix) {
        if (StringUtils.isNotBlank(delPrefix)) {
            sqlContext.setDelPrefix(delPrefix);
        }
    }

    private void setAuthor(SQLContext sqlContext, String author) {
        if (StringUtils.isNotBlank(author)) {
            sqlContext.setAuthor(author);
        }
    }

    private String doGenerator(SQLContext sqlContext, String template) {
        if (template == null) {
            return "";
        }
        VelocityContext context = new VelocityContext();
        Object pkColumn = sqlContext.getTableDefinition().getPkColumn();
        if (pkColumn == null) {
            pkColumn = Collections.emptyMap();
        }
        context.put("context", sqlContext);
        context.put("table", sqlContext.getTableDefinition());
        context.put("pk", pkColumn);
        context.put("columns", sqlContext.getTableDefinition().getColumnDefinitions());
        context.put("csharpColumns", sqlContext.getTableDefinition().getCsharpColumnDefinitions());

        return VelocityUtil.generate(context, template);
    }

}
