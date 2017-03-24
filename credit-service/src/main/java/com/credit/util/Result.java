package com.credit.util;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 接口返回结果
 * @author fuhongxing
 */
public class Result<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = -8508268287523566997L;

	/** 类型(状态) */
	private Type type;
	
	/** 消息 */
	private List<String> message;
	
	private String messages;
	/** 数据 */
	private T data;
	
	/** 状态码 */
	private String code;
	
	/** 状态 */
	private Status status;
	
	private BigDecimal creditLimitMoney; //负债总额度
	private BigDecimal alreadyUseMoney; //负债已使用额度
	private Integer threeMonthqueryCount;	//近三个月查询次数
	private Integer oneMonthqueryCount; //近一个月查询次数
	
	
	/** 构造函数 */
	public Result() {
//		message = new LinkedList<String>();
	}
	
	/**
	 * 构造函数
	 *
	 * @param type
	 */
	public Result(Type type) {
		this();
		this.type = type;
	}
	
	/**
	 * 构造函数
	 *
	 * @param type
	 * @param message
	 */
	public Result(Type type, String message) {
		this(type);
		addMessage(message);
	}
	
	/**
	 * 构造函数
	 *
	 * @param type
	 * @param message
	 * @param data
	 */
	public Result(Type type, String message, T data) {
		this(type, message);
		this.data = data;
	}
	

	public boolean success() {
		return Type.YES.equals(this.type);
	}

	/**
	 * 是否警告
	 * 
	 * @return
	 */
	public boolean warning() {
		return Type.NO.equals(this.type);
	}

	/**
	 * 读取类型
	 * 
	 * @return
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * 设置类型
	 * 
	 * @param type
	 * @return
	 */
	public Result<T> setType(Type type) {
		this.type = type;
		return this;
	}
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * 读取消息
	 * 
	 * @param key
	 * @return
	 */
	public List<String> getMessage() {
		return message;
	}
	
	/**
	 * 读取第一条消息
	 * 
	 * @return
	 */
	/*public String getFirstMessage() {
		return !messages.isEmpty() ? messages.get(0) : null;
	}*/
	
	/**
	 * 添加消息
	 * 
	 * @param message
	 * @param args
	 * @return
	 */
	public Result<T> addMessage(String message) {
		if (!StringUtils.isEmpty(message)){ 
			this.message.add(message);
		}
		return this;
	}
	
	/**
	 * 添加消息
	 * 
	 * @param index
	 * @param message
	 * @param args
	 * @return
	 */
	public Result<T> addMessage(int index, String message) {
		if (!StringUtils.isEmpty(message)){ 
			this.message.add(index, message);
		}
		return this;
	}
	
	/**
	 * 读取数据
	 * 
	 * @return
	 */
	public T getData() {
		return this.data;
	}
	
	/**
	 * 设置数据
	 * 
	 * @param data
	 * @return
	 */
	public Result<T> setData(T data) {
		this.data = data;
		return this;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * 类型
	 */
	public enum Type {
		
		/** 成功 */
		YES, 
		
		/** 警告 */
		NO
	}
	
	public enum Status {
		YES, NO;
	}
	
	public BigDecimal getCreditLimitMoney() {
		return creditLimitMoney;
	}

	public void setCreditLimitMoney(BigDecimal creditLimitMoney) {
		this.creditLimitMoney = creditLimitMoney;
	}

	public BigDecimal getAlreadyUseMoney() {
		return alreadyUseMoney;
	}

	public void setAlreadyUseMoney(BigDecimal alreadyUseMoney) {
		this.alreadyUseMoney = alreadyUseMoney;
	}

	public Integer getThreeMonthqueryCount() {
		return threeMonthqueryCount;
	}

	public void setThreeMonthqueryCount(Integer threeMonthqueryCount) {
		this.threeMonthqueryCount = threeMonthqueryCount;
	}

	public Integer getOneMonthqueryCount() {
		return oneMonthqueryCount;
	}

	public void setOneMonthqueryCount(Integer oneMonthqueryCount) {
		this.oneMonthqueryCount = oneMonthqueryCount;
	}

	public String getMessages() {
		return messages;
	}

	public void setMessages(String messages) {
		this.messages = messages;
	}
	
	
}
