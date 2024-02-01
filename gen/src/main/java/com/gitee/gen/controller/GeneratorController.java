package com.gitee.gen.controller;

import com.gitee.gen.common.Action;
import com.gitee.gen.common.GeneratorParam;
import com.gitee.gen.common.Result;
import com.gitee.gen.entity.DatasourceConfig;
import com.gitee.gen.gen.GeneratorConfig;
import com.gitee.gen.service.DatasourceConfigService;
import com.gitee.gen.service.GeneratorService;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

/**
 * @author tanghc
 */
@Controller
@Mapping("generate")
public class GeneratorController {

    @Inject
    private DatasourceConfigService datasourceConfigService;

    @Inject
    private GeneratorService generatorService;

    /**
     * 生成代码
     *
     * @param generatorParam 生成参数
     * @return 返回代码内容
     */
    @Mapping("/code")
    public Result code(GeneratorParam generatorParam) {
        int datasourceConfigId = generatorParam.getDatasourceConfigId();
        DatasourceConfig datasourceConfig = datasourceConfigService.getById(datasourceConfigId);
        GeneratorConfig generatorConfig = GeneratorConfig.build(datasourceConfig);
        return Action.ok(generatorService.generate(generatorParam, generatorConfig));
    }

}
