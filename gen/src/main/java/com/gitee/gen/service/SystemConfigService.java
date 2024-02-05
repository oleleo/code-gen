package com.gitee.gen.service;

import com.gitee.gen.entity.SystemConfig;
import com.gitee.gen.mapper.SystemConfigMapper;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Component;


@Component
public class SystemConfigService {

    @Db
    private SystemConfigMapper systemConfigMapper;

    public Optional<String> getValue(String key) {
        SystemConfig config = systemConfigMapper.getByKey(key);
        return Optional.ofNullable(config)
                .map(SystemConfig::getConfigValue);
    }

    public void saveOrUpdate(SystemConfig systemConfig) {
        SystemConfig config = systemConfigMapper.getByKey(systemConfig.getConfigKey());
        if (config == null) {
            systemConfigMapper.insert(systemConfig);
        } else {
            config.setConfigValue(systemConfig.getConfigValue());
            config.setRemark(systemConfig.getRemark());
            systemConfigMapper.update(config);
        }
    }

    /**
     * 查询所有记录
     *
     * @return 返回集合，没有返回空List
     */
    public List<SystemConfig> listAll() {
    	return systemConfigMapper.listAll();
    }


    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 返回记录，没有返回null
     */
    public SystemConfig getById(Integer id) {
    	return systemConfigMapper.getById(id);
    }

    /**
     * 新增，插入所有字段
     *
     * @param systemConfig 新增的记录
     * @return 返回影响行数
     */
    public int insert(SystemConfig systemConfig) {
    	return systemConfigMapper.insert(systemConfig);
    }

    /**
     * 新增，忽略null字段
     *
     * @param systemConfig 新增的记录
     * @return 返回影响行数
     */
    public int insertIgnoreNull(SystemConfig systemConfig) {
    	return systemConfigMapper.insertIgnoreNull(systemConfig);
    }

    /**
     * 修改，修改所有字段
     *
     * @param systemConfig 修改的记录
     * @return 返回影响行数
     */
    public int update(SystemConfig systemConfig) {
    	return systemConfigMapper.update(systemConfig);
    }

    /**
     * 修改，忽略null字段
     *
     * @param systemConfig 修改的记录
     * @return 返回影响行数
     */
    public int updateIgnoreNull(SystemConfig systemConfig) {
    	return systemConfigMapper.updateIgnoreNull(systemConfig);
    }

    /**
     * 删除记录
     *
     * @param systemConfig 待删除的记录
     * @return 返回影响行数
     */
    public int delete(SystemConfig systemConfig) {
    	return systemConfigMapper.delete(systemConfig);
    }

}
