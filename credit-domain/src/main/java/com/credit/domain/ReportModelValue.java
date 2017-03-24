package com.credit.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 报告字段解析模板对应表达式值
 * 
 * @author Alan Fu
 */
@Entity
@Table(name="report_model_value")
public class ReportModelValue extends AbstractEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long reportModelId;// 模板表id

	private String value;// 模板表达式值

	private String valueIndex;// 报文字段解析位置

	private Date createdate;// 创建时间

	private String type;

	public Long getReportModelId() {
		return reportModelId;
	}

	public void setReportModelId(Long reportModelId) {
		this.reportModelId = reportModelId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValueIndex() {
		return valueIndex;
	}

	public void setValueIndex(String valueIndex) {
		this.valueIndex = valueIndex;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
}