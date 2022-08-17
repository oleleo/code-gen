package com.gitee.gen.gen.dm;

import com.gitee.gen.gen.GeneratorConfig;
import com.gitee.gen.gen.SQLService;
import com.gitee.gen.gen.TableSelector;

public class DmService implements SQLService {

	@Override
	public TableSelector getTableSelector(GeneratorConfig generatorConfig) {
		return new DmTableSelector(new DmColumnSelector(generatorConfig), generatorConfig);
	}

}
