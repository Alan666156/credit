package com.credit.common;


/**
 * 贷款类型
 * @author fuhongxing
 */
public enum CreditType {
	个人住房贷款("个人住房贷款", "T0001"),	
	个人住房公积金贷款("个人住房公积金贷款", "T0002"),	
	个人经营性贷款("个人经营性贷款", "T0003"),	
	个人消费贷款("个人消费贷款", "T0004"),
	个人汽车贷款("个人汽车贷款", "T0005"),
	个人助学贷款("个人助学贷款", "T0006"),
	个人商用房贷款("个人商用房（包括商住两用）贷款", "T0007"),
	购房贷款("购房贷款", "T0008"),
	农户贷款("农户贷款", "T0009"),
	其他贷款("其他贷款", "T0010");
	
	private CreditType(String value, String name) {
		this.value = value;
		this.name = name;
	}
	
	private String name;
	private String value;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public static String getNameByCode(String name) {
		for (CreditType c : CreditType.values()) {
			if (c.getName() == name) {
				return c.value;
			}
		}
		return null;
	}
	public static void main(String[] args) {
		System.out.println(CreditType.个人住房公积金贷款.getName());
	}
}
