package com.credit.util;


/**
 * 客户查询历史状态枚举
 * @author Alan Fu
 */
public enum CustomerHistorEnum {
	
	REPORT_ALREADY_GET("1","已获取"),
	REPORT_ISNOT("2","无报告"),
	REPORT_OUT_OF_DATE("3","已过期");
	
	private final String code;
	private final String desc;
	
	private CustomerHistorEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}
	
	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}
	
}
