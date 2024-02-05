package com.gitee.gen.entity;


/**
 * 系统配置表
 */
public class SystemConfig {
	private Integer id;
	/** 配置项 */
	private String configKey;
	/** 配置值 */
	private String configValue;
	/** 备注 */
	private String remark;

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return this.id;
	}

	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}

	public String getConfigKey() {
		return this.configKey;
	}

	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}

	public String getConfigValue() {
		return this.configValue;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRemark() {
		return this.remark;
	}


	@Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) {return false;}
        SystemConfig that = (SystemConfig) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SystemConfig{" +
		",id='" + id + "'" +
		",configKey='" + configKey + "'" +
		",configValue='" + configValue + "'" +
		",remark='" + remark + "'" +
                '}';
    }

}
