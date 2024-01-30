package com.gitee.gen.entity;


/**
 * 字段类型配置表
 */
public class TypeConfig {
	private Integer id;
	/** 数据库类型 */
	private String dbType;
	/** 基本类型 */
	private String baseType;
	/** 装箱类型 */
	private String boxType;

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return this.id;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getDbType() {
		return this.dbType;
	}

	public void setBaseType(String baseType) {
		this.baseType = baseType;
	}

	public String getBaseType() {
		return this.baseType;
	}

	public void setBoxType(String boxType) {
		this.boxType = boxType;
	}

	public String getBoxType() {
		return this.boxType;
	}


	@Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) {return false;}
        TypeConfig that = (TypeConfig) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TypeConfig{" +
				"id=" + id +
						",dbType='" + dbType + "'" +
						",baseType='" + baseType + "'" +
						",boxType='" + boxType + "'" +
		                '}';
    }

}
