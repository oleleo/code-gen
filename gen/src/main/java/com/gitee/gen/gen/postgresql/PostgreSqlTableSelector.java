package com.gitee.gen.gen.postgresql;

import com.gitee.gen.gen.ColumnSelector;
import com.gitee.gen.gen.GeneratorConfig;
import com.gitee.gen.gen.TableDefinition;
import com.gitee.gen.gen.TableSelector;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

import static com.gitee.gen.util.FieldUtil.convertString;

/**
 * @author tanghc
 */
public class PostgreSqlTableSelector extends TableSelector {

    private static final String DEFAULT_SCHEMA = "public";

    public PostgreSqlTableSelector(ColumnSelector columnSelector, GeneratorConfig generatorConfig) {
        super(columnSelector, generatorConfig);
    }

    private static final String SHOW_TABLE_SQL =
            "SELECT " +
                    " t.schemaname, t.tablename, t2.cmt " +
                    "FROM " +
                    " pg_tables t LEFT JOIN  " +
                    " ( " +
                    " SELECT relname as tablename, obj_description(oid) AS cmt " +
                    " FROM pg_class C " +
                    " WHERE relkind='r' AND relname NOT LIKE 'pg_%%' AND relname NOT LIKE 'sql_%%' AND relchecks=0  " +
                    " ORDER BY relname  " +
                    ") t2 ON t.tablename = t2.tablename " +
                    "WHERE schemaname != 'pg_catalog' AND schemaname != 'information_schema' %s %s";

    @Override
    protected String getShowTablesSQL(GeneratorConfig generatorConfig) {
        String schema = generatorConfig.getSchemaName();
        if (schema == null) {
            schema = "";
        }
        if (StringUtils.isNotBlank(schema)) {
            schema = String.format("AND schemaname = '%s'", schema);
        }
        List<String> tableNames = wrapTableNames();
        String and = "";
        if (!tableNames.isEmpty()) {
            and = String.format("AND t.tablename in (%s)", String.join(",", tableNames));
        }
        return String.format(SHOW_TABLE_SQL, schema, and);
    }

    @Override
    protected TableDefinition buildTableDefinition(Map<String, Object> tableMap) {
        TableDefinition tableDefinition = new TableDefinition();
        tableDefinition.setSchema(convertString(tableMap.get("SCHEMANAME")));
        tableDefinition.setTableName(convertString(tableMap.get("TABLENAME")));
        tableDefinition.setComment(convertString(tableMap.get("CMT")));
        return tableDefinition;
    }
}
