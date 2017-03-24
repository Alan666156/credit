package com.credit.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 报告字段解析模板
 * @author Alan Fu
 */
@Entity
@Table(name="report_model")
public class ReportModel extends AbstractEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String type;// 解析类型(1:信用卡;2:贷款类)

	private String name;// 字段名称

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type == null ? null : type.trim();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

}