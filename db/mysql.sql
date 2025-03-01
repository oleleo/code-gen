CREATE DATABASE IF NOT EXISTS `gen` DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
USE `gen`;


-- ----------------------------
-- Table structure for datasource_config
-- ----------------------------
DROP TABLE IF EXISTS `datasource_config`;
CREATE TABLE `datasource_config`
(
    `id`           int(11) UNSIGNED                                        NOT NULL AUTO_INCREMENT,
    `db_type`      int(11)                                                 NOT NULL DEFAULT 0 COMMENT '数据库类型，1：MySql, 2:Oracle, 3:sqlserver',
    `driver_class` varchar(64)   NULL     DEFAULT '' COMMENT '数据库驱动',
    `db_name`      varchar(64)   NOT NULL DEFAULT '' COMMENT '数据库名称',
    `host`         varchar(64)   NOT NULL DEFAULT '' COMMENT '数据库host',
    `port`         int(11)                                                 NOT NULL DEFAULT 0 COMMENT '数据库端口',
    `username`     varchar(64)   NOT NULL DEFAULT '' COMMENT '数据库用户名',
    `password`     varchar(64)   NOT NULL DEFAULT '' COMMENT '数据库密码',
    `is_deleted`   int(11)                                                 NOT NULL DEFAULT 0 COMMENT '是否已删除，1：已删除，0：未删除',
    `package_name` varchar(100)  NULL     DEFAULT NULL,
    `del_prefix`   varchar(200)  NULL     DEFAULT NULL,
    `group_id`     int(11)                                                 NULL     DEFAULT NULL,
    `schema_name`  varchar(100)  NULL     DEFAULT NULL,
    `author`       varchar(255)  NULL     DEFAULT NULL,
    `db_desc`       varchar(64)   null     default null comment '数据库别名',
    `db_group_name` varchar(64)   null     default null comment '数据库分组名称',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT = '数据源配置表';

-- ----------------------------
-- Records of datasource_config
-- ----------------------------

-- ----------------------------
-- Table structure for generate_history
-- ----------------------------
DROP TABLE IF EXISTS `generate_history`;
CREATE TABLE `generate_history`
(
    `id`             int(11)                                                NOT NULL AUTO_INCREMENT,
    `config_content` text         NULL,
    `md5_value`      varchar(64)  NULL DEFAULT NULL,
    `generate_time`  datetime                                               NOT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT='生成历史';

-- ----------------------------
-- Records of generate_history
-- ----------------------------

-- ----------------------------
-- Table structure for template_config
-- ----------------------------
DROP TABLE IF EXISTS `template_config`;
CREATE TABLE `template_config`
(
    `id`         int(11) UNSIGNED                                        NOT NULL AUTO_INCREMENT,
    `name`       varchar(64)   NOT NULL DEFAULT '' COMMENT '模板名称',
    `folder`     varchar(64)   NOT NULL DEFAULT '' COMMENT '目录名称',
    `file_name`  varchar(128)  NOT NULL DEFAULT '' COMMENT '文件名称',
    `content`    text          NOT NULL COMMENT '内容',
    `is_deleted` int(11)                                                 NOT NULL DEFAULT 0 COMMENT '是否删除，1：已删除，0：未删除',
    `group_id`   int(11)                                                 NULL     DEFAULT NULL,
    `group_name` varchar(100)  NULL     DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT='模板表';

-- ----------------------------
-- Records of template_config
-- ----------------------------
INSERT INTO `template_config`
VALUES (1, 'entity', '', '${context.javaBeanName}.java',
        'package ${context.packageName}.entity;\n\n#if(${table.hasDateField})\nimport java.util.Date;\n#end\n#if(${table.hasLocalDateField})\nimport java.time.LocalDate;\n#end\n#if(${table.hasLocalDateTimeField})\nimport java.time.LocalDateTime;\n#end\n#if(${table.hasBigDecimalField})\nimport java.math.BigDecimal;\n#end\n\n#if( \"${table.comment}\" != \"\" )\n/**\n * ${table.comment}\n */\n#end\npublic class ${context.javaBeanName} {\n#foreach($column in $columns)\n#if( \"${column.comment}\" != \"\" )\n	/** ${column.comment} */\n#end\n	private ${column.javaTypeBox} ${column.javaFieldName};\n#end\n\n#foreach(${column} in ${columns})\n	public void set${column.javaFieldNameUF}(${column.javaTypeBox} ${column.javaFieldName}) {\n		this.${column.javaFieldName} = ${column.javaFieldName};\n	}\n	\n	public ${column.javaTypeBox} get${column.javaFieldNameUF}() {\n		return this.${column.javaFieldName};\n	}\n	\n#end\n\n	@Override\n    public boolean equals(Object o) {\n        if (this == o) { return true; }\n        if (o == null || getClass() != o.getClass()) {return false;}\n        ${context.javaBeanName} that = (${context.javaBeanName}) o;\n        return ${pk.javaFieldName}.equals(that.${pk.javaFieldName});\n    }\n\n    @Override\n    public int hashCode() {\n        return java.util.Objects.hash(${pk.javaFieldName});\n    }\n    \n    @Override\n    public String toString() {\n        return \"${context.javaBeanName}{\" +\n#foreach(${column} in ${columns})\n		#if($velocityCount == 1)\n		\"${column.javaFieldName}=\" + ${column.javaFieldName} +\n		#else\n		\",${column.javaFieldName}=\'\" + ${column.javaFieldName} + \"\'\" + \n		#end\n#end\n                \'}\';\n    }\n	\n}',
        0, 1, 'default');
INSERT INTO `template_config`
VALUES (2, 'mybatis', '', '${context.javaBeanName}Mapper.xml',
        '#set($jq=\"$\")\n<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<!DOCTYPE  mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n<mapper namespace=\"${context.packageName}.mapper.${context.javaBeanName}Mapper\">\n	<resultMap id=\"BaseResultMap\" type=\"${context.packageName}.entity.${context.javaBeanName}\">\n    #foreach($column in $columns)    \n        <result column=\"${column.columnName}\" property=\"${column.javaFieldName}\" />\n	#end\n	</resultMap>  \n    \n    <!-- 表字段 -->\n    <sql id=\"baseColumns\">\n    #foreach($column in $columns)\n    #if($velocityCount > 1),#end t.${column.columnName}\n    #end\n    </sql> \n    \n    <!-- 查询全部 -->\n    <select id=\"listAll\" resultMap=\"BaseResultMap\">\n		SELECT         \n        	<include refid=\"baseColumns\" />\n		FROM ${table.tableName} t\n	</select>\n \n 	<!-- 根据主键获取单条记录 -->\n    <select id=\"getById\" resultMap=\"BaseResultMap\" parameterType=\"${pk.javaTypeBox}\">\n		SELECT         \n        	<include refid=\"baseColumns\" />\n		FROM ${table.tableName} t\n		WHERE ${pk.columnName} = #{${pk.javaFieldName}}\n	</select>\n\n	<!-- 插入全部字段 -->\n    <insert id=\"insert\" parameterType=\"${context.packageName}.entity.${context.javaBeanName}\"\n		keyProperty=\"${context.javaPkName}\" keyColumn=\"${context.pkName}\" useGeneratedKeys=\"true\"\n    >\n	INSERT INTO ${table.tableName}\n    <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">	 \n    #foreach($column in $columns) \n    	#if(!${column.isIdentityPk}) \n    	${column.columnName},\n    	#end          \n    #end\n    </trim>\n    <trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">            \n    #foreach($column in $columns) \n    	#if(!${column.isIdentityPk})                     \n        #{${column.javaFieldName}},                   \n    	#end          \n    #end\n    </trim>\n	</insert>\n    \n    <!-- 插入不为NULL的字段 -->\n    <insert id=\"insertIgnoreNull\" parameterType=\"${context.packageName}.entity.${context.javaBeanName}\"\n        keyProperty=\"${pk.javaFieldName}\" keyColumn=\"${context.pkName}\" useGeneratedKeys=\"true\"\n        >\n        INSERT INTO ${table.tableName}    \n        <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">	 \n\n            #foreach($column in $columns) \n                #if(!${column.isIdentityPk}) \n                    <if test=\"${column.javaFieldName} != null\">\n                   ${column.columnName},\n                    </if>\n                #end          \n            #end\n        </trim>\n        <trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">            \n            #foreach($column in $columns) \n                #if(!${column.isIdentityPk})  \n                    <if test=\"${column.javaFieldName} != null\" >\n                    #{${column.javaFieldName}},                    \n                    </if>\n                #end          \n            #end\n        </trim>\n    </insert>\n\n	<!-- 更新,更新全部字段 -->\n    <update id=\"update\" parameterType=\"${context.packageName}.entity.${context.javaBeanName}\">\n    UPDATE ${table.tableName}\n     <set>		\n     #foreach($column in $columns) \n         #if(!${column.isPk})               \n         ${column.columnName}=#{${column.javaFieldName}},        \n         #end          \n     #end\n     </set>	\n    WHERE ${pk.columnName} = #{${pk.javaFieldName}}\n    </update>  \n    \n    \n    <!-- 更新不为NULL的字段 -->\n    <update id=\"updateIgnoreNull\" parameterType=\"${context.packageName}.entity.${context.javaBeanName}\">\n    UPDATE ${table.tableName}\n    <set>\n    	#foreach($column in $columns) \n            #if(!${column.isPk})  \n                <if test=\"${column.javaFieldName} != null\" >\n                ${column.columnName}=#{${column.javaFieldName}},                 \n                </if>\n            #end          \n        #end\n    </set>\n    WHERE ${pk.columnName} = #{${pk.javaFieldName}}\n    </update>\n\n		\n	<!-- 根据主键删除记录 -->\n	<delete id=\"delete\" parameterType=\"${context.packageName}.entity.${context.javaBeanName}\">\n		UPDATE ${table.tableName}\n		SET is_deleted=1\n		WHERE ${pk.columnName} = #{${pk.javaFieldName}}\n	</delete>\n\n\n</mapper>',
        0, 1, 'default');
INSERT INTO `template_config`
VALUES (3, 'dao', '', '${context.javaBeanName}Mapper.java',
        'package ${context.packageName}.mapper;\n\nimport java.util.List;\nimport org.apache.ibatis.annotations.Mapper;\nimport ${context.packageName}.entity.${context.javaBeanName};\n\n@Mapper\npublic interface ${context.javaBeanName}Mapper {\n\n	/**\n     * 查询所有记录\n     *\n     * @return 返回集合，没有返回空List\n     */\n	List<${context.javaBeanName}> listAll();\n\n\n	/**\n     * 根据主键查询\n     *\n     * @param id 主键\n     * @return 返回记录，没有返回null\n     */\n	${context.javaBeanName} getById(${pk.javaTypeBox} ${pk.javaFieldName});\n	\n	/**\n     * 新增，插入所有字段\n     *\n     * @param ${context.javaBeanNameLF} 新增的记录\n     * @return 返回影响行数\n     */\n	int insert(${context.javaBeanName} ${context.javaBeanNameLF});\n	\n	/**\n     * 新增，忽略null字段\n     *\n     * @param ${context.javaBeanNameLF} 新增的记录\n     * @return 返回影响行数\n     */\n	int insertIgnoreNull(${context.javaBeanName} ${context.javaBeanNameLF});\n	\n	/**\n     * 修改，修改所有字段\n     *\n     * @param ${context.javaBeanNameLF} 修改的记录\n     * @return 返回影响行数\n     */\n	int update(${context.javaBeanName} ${context.javaBeanNameLF});\n	\n	/**\n     * 修改，忽略null字段\n     *\n     * @param ${context.javaBeanNameLF} 修改的记录\n     * @return 返回影响行数\n     */\n	int updateIgnoreNull(${context.javaBeanName} ${context.javaBeanNameLF});\n	\n	/**\n     * 删除记录\n     *\n     * @param ${context.javaBeanNameLF} 待删除的记录\n     * @return 返回影响行数\n     */\n	int delete(${context.javaBeanName} ${context.javaBeanNameLF});\n	\n}',
        0, 1, 'default');
INSERT INTO `template_config`
VALUES (4, 'service', '', '${context.javaBeanName}Service',
        'package ${context.packageName}.mapper;\n\nimport ${context.packageName}.entity.${context.javaBeanName};\nimport ${context.packageName}.mapper.${context.javaBeanName}Mapper;\nimport org.springframework.beans.factory.annotation.Autowired;\nimport org.springframework.stereotype.Service;\n\nimport java.util.List;\n\n@Service\npublic class ${context.javaBeanName}Service {\n\n    @Autowired\n    private ${context.javaBeanName}Mapper ${context.javaBeanNameLF}Mapper;\n\n    /**\n     * 查询所有记录\n     *\n     * @return 返回集合，没有返回空List\n     */\n    public List<${context.javaBeanName}> listAll() {\n    	return ${context.javaBeanNameLF}Mapper.listAll();\n    }\n\n\n    /**\n     * 根据主键查询\n     *\n     * @param id 主键\n     * @return 返回记录，没有返回null\n     */\n    public ${context.javaBeanName} getById(${pk.javaTypeBox} ${pk.javaFieldName}) {\n    	return ${context.javaBeanNameLF}Mapper.getById(${pk.javaFieldName});\n    }\n	\n    /**\n     * 新增，插入所有字段\n     *\n     * @param ${context.javaBeanNameLF} 新增的记录\n     * @return 返回影响行数\n     */\n    public int insert(${context.javaBeanName} ${context.javaBeanNameLF}) {\n    	return ${context.javaBeanNameLF}Mapper.insert(${context.javaBeanNameLF});\n    }\n	\n    /**\n     * 新增，忽略null字段\n     *\n     * @param ${context.javaBeanNameLF} 新增的记录\n     * @return 返回影响行数\n     */\n    public int insertIgnoreNull(${context.javaBeanName} ${context.javaBeanNameLF}) {\n    	return ${context.javaBeanNameLF}Mapper.insertIgnoreNull(${context.javaBeanNameLF});\n    }\n	\n    /**\n     * 修改，修改所有字段\n     *\n     * @param ${context.javaBeanNameLF} 修改的记录\n     * @return 返回影响行数\n     */\n    public int update(${context.javaBeanName} ${context.javaBeanNameLF}) {\n    	return ${context.javaBeanNameLF}Mapper.update(${context.javaBeanNameLF});\n    }\n	\n    /**\n     * 修改，忽略null字段\n     *\n     * @param ${context.javaBeanNameLF} 修改的记录\n     * @return 返回影响行数\n     */\n    public int updateIgnoreNull(${context.javaBeanName} ${context.javaBeanNameLF}) {\n    	return ${context.javaBeanNameLF}Mapper.updateIgnoreNull(${context.javaBeanNameLF});\n    }\n	\n    /**\n     * 删除记录\n     *\n     * @param ${context.javaBeanNameLF} 待删除的记录\n     * @return 返回影响行数\n     */\n    public int delete(${context.javaBeanName} ${context.javaBeanNameLF}) {\n    	return ${context.javaBeanNameLF}Mapper.delete(${context.javaBeanNameLF});\n    }\n	\n}',
        0, 1, 'default');
INSERT INTO `template_config`
VALUES (5, 'controller', '', '${context.javaBeanName}Controller',
        'package ${context.packageName}.mapper;\n\nimport java.util.List;\nimport org.springframework.beans.factory.annotation.Autowired;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RequestBody;\nimport org.springframework.web.bind.annotation.RestController;\n\nimport ${context.packageName}.entity.${context.javaBeanName};\nimport ${context.packageName}.service.${context.javaBeanName}Service;\n\n@RestController\npublic class ${context.javaBeanName}Controller {\n\n    @Autowired\n    private ${context.javaBeanName}Service ${context.javaBeanNameLF}Service;\n\n    /**\n     * 查询所有记录\n     *\n     * @return 返回集合，没有返回空List\n     */\n    @RequestMapping(\"list\")\n    public List<${context.javaBeanName}> listAll() {\n        return ${context.javaBeanNameLF}Service.listAll();\n    }\n\n\n    /**\n     * 根据主键查询\n     *\n     * @param id 主键\n     * @return 返回记录，没有返回null\n     */\n    @RequestMapping(\"getById\")\n    public ${context.javaBeanName} getById(${pk.javaTypeBox} ${pk.javaFieldName}) {\n        return ${context.javaBeanNameLF}Service.getById(${pk.javaFieldName});\n    }\n    \n    /**\n     * 新增，插入所有字段\n     *\n     * @param ${context.javaBeanNameLF} 新增的记录\n     * @return 返回影响行数\n     */\n    @RequestMapping(\"insert\")\n    public int insert(@RequestBody ${context.javaBeanName} ${context.javaBeanNameLF}) {\n        return ${context.javaBeanNameLF}Service.insert(${context.javaBeanNameLF});\n    }\n    \n    /**\n     * 新增，忽略null字段\n     *\n     * @param ${context.javaBeanNameLF} 新增的记录\n     * @return 返回影响行数\n     */\n    @RequestMapping(\"insert\")\n    public int insertIgnoreNull(@RequestBody ${context.javaBeanName} ${context.javaBeanNameLF}) {\n        return ${context.javaBeanNameLF}Service.insertIgnoreNull(${context.javaBeanNameLF});\n    }\n    \n    /**\n     * 修改，修改所有字段\n     *\n     * @param ${context.javaBeanNameLF} 修改的记录\n     * @return 返回影响行数\n     */\n    @RequestMapping(\"update\")\n    public int update(@RequestBody ${context.javaBeanName} ${context.javaBeanNameLF}) {\n        return ${context.javaBeanNameLF}Service.update(${context.javaBeanNameLF});\n    }\n    \n    /**\n     * 修改，忽略null字段\n     *\n     * @param ${context.javaBeanNameLF} 修改的记录\n     * @return 返回影响行数\n     */\n    @RequestMapping(\"updateIgnoreNull\")\n    public int updateIgnoreNull(@RequestBody ${context.javaBeanName} ${context.javaBeanNameLF}) {\n        return ${context.javaBeanNameLF}Service.updateIgnoreNull(${context.javaBeanNameLF});\n    }\n    \n    /**\n     * 删除记录\n     *\n     * @param ${context.javaBeanNameLF} 待删除的记录\n     * @return 返回影响行数\n     */\n    @RequestMapping(\"delete\")\n    public int delete(@RequestBody ${context.javaBeanName} ${context.javaBeanNameLF}) {\n        return ${context.javaBeanNameLF}Service.delete(${context.javaBeanNameLF});\n    }\n    \n}',
        0, 1, 'default');

-- ----------------------------
-- Table structure for template_group
-- ----------------------------
DROP TABLE IF EXISTS `template_group`;
CREATE TABLE `template_group`
(
    `id`         int(11) UNSIGNED                                        NOT NULL AUTO_INCREMENT,
    `group_name` varchar(100)  NULL DEFAULT NULL COMMENT '模板组名称',
    `is_deleted` int(11)                                                 NULL DEFAULT 0 COMMENT '是否删除，1：已删除，0：未删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT = '模板组表';

-- ----------------------------
-- Records of template_group
-- ----------------------------
INSERT INTO `template_group`
VALUES (1, 'default', 0);


DROP TABLE IF EXISTS `type_config`;
CREATE TABLE `type_config`
(
    `id`         int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `db_type` varchar(64)  NULL DEFAULT NULL COMMENT '数据库类型',
    `base_type` varchar(64)  NULL DEFAULT NULL COMMENT '基本类型',
    `box_type` varchar(64)  NULL DEFAULT NULL COMMENT '装箱类型',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT = '字段类型配置表';

DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config`
(
    `id`         int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `config_key` varchar(64)  NULL DEFAULT NULL COMMENT '配置项',
    `config_value` varchar(256)  NULL DEFAULT NULL COMMENT '配置值',
    `remark` varchar(128)  NULL DEFAULT NULL COMMENT '备注',
    UNIQUE KEY `uk_config_key` (`config_key`) USING BTREE,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT = '系统配置表';

insert into type_config(db_type, base_type, box_type) values
('bit', 'boolean', 'Boolean')
,('boolean', 'boolean', 'Boolean')
,('tinyint', 'int', 'Integer')
,('smallint', 'int', 'Integer')
,('int', 'int', 'Integer')
,('bigint', 'long', 'Long')
,('float', 'float', 'Float')
,('double', 'double', 'Double')
,('decimal', 'BigDecimal', 'BigDecimal')
,('varchar', 'String', 'String')
,('datetime', 'Date', 'Date')
,('date', 'Date', 'Date')
,('blob', 'String', 'String')
,('jsonb', 'String', 'String')
;
