package com.gitee.gen.service;

import com.gitee.gen.entity.TypeConfig;
import com.gitee.gen.gen.converter.ColumnTypeConverter;
import com.gitee.gen.gen.converter.DbColumnTypeConverter;
import com.gitee.gen.mapper.TypeConfigMapper;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TypeConfigService {

    @Db
    private TypeConfigMapper typeConfigMapper;

    /**
     * 查询所有记录
     *
     * @return 返回集合，没有返回空List
     */
    public List<TypeConfig> listAll() {
    	return typeConfigMapper.listAll();
    }


    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 返回记录，没有返回null
     */
    public TypeConfig getById(Integer id) {
    	return typeConfigMapper.getById(id);
    }

    /**
     * 新增，插入所有字段
     *
     * @param typeConfig 新增的记录
     * @return 返回影响行数
     */
    public int insert(TypeConfig typeConfig) {
    	return typeConfigMapper.insert(typeConfig);
    }

    /**
     * 新增，忽略null字段
     *
     * @param typeConfig 新增的记录
     * @return 返回影响行数
     */
    public int insertIgnoreNull(TypeConfig typeConfig) {
    	return typeConfigMapper.insertIgnoreNull(typeConfig);
    }

    /**
     * 修改，修改所有字段
     *
     * @param typeConfig 修改的记录
     * @return 返回影响行数
     */
    public int update(TypeConfig typeConfig) {
    	return typeConfigMapper.update(typeConfig);
    }

    /**
     * 修改，忽略null字段
     *
     * @param typeConfig 修改的记录
     * @return 返回影响行数
     */
    public int updateIgnoreNull(TypeConfig typeConfig) {
    	return typeConfigMapper.updateIgnoreNull(typeConfig);
    }

    /**
     * 删除记录
     *
     * @param typeConfig 待删除的记录
     * @return 返回影响行数
     */
    public int delete(TypeConfig typeConfig) {
    	return typeConfigMapper.delete(typeConfig);
    }

    public ColumnTypeConverter buildColumnTypeConverter() {
        List<TypeConfig> typeConfigs = typeConfigMapper.listAll();
        Map<String, TypeConfig> map = typeConfigs.stream()
                .collect(Collectors.toMap(TypeConfig::getDbType, Function.identity(), (v1, v2) -> v2));
        return new DbColumnTypeConverter(map);
    }
}
