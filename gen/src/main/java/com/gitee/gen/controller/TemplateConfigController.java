package com.gitee.gen.controller;

import com.gitee.gen.common.Action;
import com.gitee.gen.common.Result;
import com.gitee.gen.entity.TemplateConfig;
import com.gitee.gen.entity.TemplateGroup;
import com.gitee.gen.service.TemplateConfigService;
import com.gitee.gen.service.TemplateGroupService;
import com.gitee.gen.util.TemplateMetaUtils;
import org.apache.commons.lang.StringUtils;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Path;

import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Controller
@Mapping("template")
public class TemplateConfigController {

    @Inject
    private TemplateConfigService templateConfigService;

    @Inject
    private TemplateGroupService templateGroupService;

    @Mapping("/add")
    public Result add(TemplateConfig templateConfig) {
        templateConfigService.insert(templateConfig);
        return Action.ok(templateConfig);
    }

    @Mapping("/get/{id}")
    public Result get(@Path("id") int id) {
        return Action.ok(templateConfigService.getById(id));
    }

    @Mapping("/list")
    public Result list(String groupId) {
        List<TemplateConfig> templateConfigs = null;
        if(StringUtils.isEmpty(groupId)){
            templateConfigs = templateConfigService.listAll();
        }else {
            templateConfigs = templateConfigService.listByGroupId(groupId);
        }
        Map<Integer, String> idMap = templateGroupService.listAll()
                .stream()
                .collect(Collectors.toMap(TemplateGroup::getId, TemplateGroup::getGroupName));
        for (TemplateConfig templateConfig : templateConfigs) {
            Integer gid = templateConfig.getGroupId();
            if (gid != null) {
                String groupName = idMap.getOrDefault(gid, "");
                templateConfig.setGroupName(groupName);
            }
            templateConfig.setContent(TemplateMetaUtils.generateMetaContent(templateConfig));
        }
        return Action.ok(templateConfigs);
    }

    @Mapping("/update")
    public Result update(TemplateConfig templateConfig) {
        templateConfigService.update(templateConfig);
        return Action.ok();
    }

    @Mapping("/del")
    public Result del(TemplateConfig templateConfig) {
        templateConfigService.delete(templateConfig);
        return Action.ok();
    }

    @Mapping("/save")
    public Result save(TemplateConfig templateConfig) {
        templateConfigService.save(templateConfig);
        return Action.ok();
    }

    @Mapping("/copy")
    public Result copy(TemplateConfig templateConfig) {
        templateConfigService.copy(templateConfig);
        return Action.ok();
    }

}
