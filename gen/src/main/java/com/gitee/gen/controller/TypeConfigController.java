package com.gitee.gen.controller;

import com.gitee.gen.common.Action;
import com.gitee.gen.common.Result;
import com.gitee.gen.entity.TypeConfig;
import com.gitee.gen.service.TypeConfigService;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

import java.util.List;

@Controller
@Mapping("type")
public class TypeConfigController {

    @Inject
    private TypeConfigService typeConfigService;

    /**
     * 查询所有记录
     *
     * @return 返回集合，没有返回空List
     */
    @Mapping("list")
    public Result listAll() {
        return Action.ok(typeConfigService.listAll());
    }


    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 返回记录，没有返回null
     */
    @Mapping("getById")
    public TypeConfig getById(Integer id) {
        return typeConfigService.getById(id);
    }

    /**
     * 修改，忽略null字段
     *
     * @param typeConfigList 修改的记录
     * @return 返回影响行数
     */
    @Mapping("update")
    public Result update(List<TypeConfig> typeConfigList) {
        for (TypeConfig typeConfig : typeConfigList) {
            typeConfigService.updateIgnoreNull(typeConfig);
        }
        return Action.ok();
    }

}
